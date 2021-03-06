package com.bakdata.conquery.models.concepts.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.validation.Valid;

import com.bakdata.conquery.io.jackson.serializer.NsIdRef;
import com.bakdata.conquery.models.concepts.Connector;
import com.bakdata.conquery.models.concepts.conditions.CTCondition;
import com.bakdata.conquery.models.concepts.filters.Filter;
import com.bakdata.conquery.models.datasets.Column;
import com.bakdata.conquery.models.datasets.Table;
import com.bakdata.conquery.models.events.Bucket;
import com.bakdata.conquery.models.events.BucketEntry;
import com.bakdata.conquery.models.events.CBlock;
import com.bakdata.conquery.models.events.MajorTypeId;
import com.bakdata.conquery.models.events.stores.root.StringStore;
import com.bakdata.conquery.models.exceptions.ConceptConfigurationException;
import com.bakdata.conquery.util.CalculatedValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.dropwizard.validation.ValidationMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter @Setter
@Slf4j
public class ConceptTreeConnector extends Connector {

	private static final long serialVersionUID = 1L;

	@NsIdRef @CheckForNull
	private Table table;

	@NsIdRef @CheckForNull
	private Column column = null;

	private CTCondition condition = null;

	@Valid @JsonManagedReference
	private List<Filter<?>> filters = new ArrayList<>();

	@JsonIgnore
	@ValidationMethod(message = "Table and Column usage are exclusive")
	public boolean isTableXOrColumn() {
		if(table != null){
			return column == null;
		}

		return column != null;
	}

	@JsonIgnore
	@ValidationMethod(message = "Column is not STRING.")
	public boolean isColumnForTree(){
		return column == null || column.getType().equals(MajorTypeId.STRING);
	}

	@Override @JsonIgnore
	public Table getTable() {
		if(column != null){
			return column.getTable();
		}

		return table;
	}

	@Override
	public List<Filter<?>> collectAllFilters() {
		return filters;
	}

	@Override
	public TreeConcept getConcept() {
		return (TreeConcept) super.getConcept();
	}

	@Override
	public void calculateCBlock(CBlock cBlock, Bucket bucket) {

		final Column column = getColumn();

		final TreeConcept treeConcept = getConcept();

		final StringStore stringStore;

		// If we have a column and it is of string-type, we create indices and caches.
		if (column != null && bucket.getStores()[column.getPosition()] instanceof StringStore) {

			stringStore = (StringStore) bucket.getStores()[column.getPosition()];

			// Create index and insert into Tree.
			TreeChildPrefixIndex.putIndexInto(treeConcept);

			treeConcept.initializeIdCache(stringStore, bucket.getImp());
		}
		// No column only possible if we have just one tree element!
		else if(treeConcept.countElements() == 1){
			stringStore = null;
		}
		else {
			throw new IllegalStateException(String.format("Cannot build tree over Connector[%s] without Column", getId()));
		}


		final int[][] mostSpecificChildren = new int[bucket.getNumberOfEvents()][];

		final ConceptTreeCache cache = treeConcept.getCache(bucket.getImp());

		final int[] root = getConcept().getPrefix();

		for (BucketEntry entry : bucket.entries()) {
			try {
				final int event = entry.getEvent();

				// Events without values are omitted
				// Events can also be filtered, allowing a single table to be used by multiple connectors.
				if (column != null && !bucket.has(event, column)) {
					mostSpecificChildren[event] = Connector.NOT_CONTAINED;
					continue;
				}
				String stringValue = "";
				int valueIndex = -1;

				if (stringStore != null) {
					valueIndex = bucket.getString(event, column);
					stringValue = stringStore.getElement(valueIndex);
				}

				// Lazy evaluation of map to avoid allocations if possible.
				final CalculatedValue<Map<String, Object>> rowMap = new CalculatedValue<>(() -> bucket.calculateMap(event));


				if ((getCondition() != null && !getCondition().matches(stringValue, rowMap))) {
					mostSpecificChildren[event] = Connector.NOT_CONTAINED;
					continue;
				}

				ConceptTreeChild child = cache == null
										 ? treeConcept.findMostSpecificChild(stringValue, rowMap)
										 : cache.findMostSpecificChild(valueIndex, stringValue, rowMap);

				// All unresolved elements resolve to the root.
				if (child == null) {
					mostSpecificChildren[event] = root;
					continue;
				}

				// put path into event
				mostSpecificChildren[event] = child.getPrefix();

				// also add concepts into bloom filter of entity cblock.
				ConceptTreeNode<?> it = child;
				while (it != null) {
					cBlock.addIncludedConcept(entry.getEntity(), it);
					it = it.getParent();
				}
			}
			catch (ConceptConfigurationException ex) {
				log.error("Failed to resolve event " + bucket + "-" + entry.getEvent() + " against concept " + this, ex);
			}
		}

		cBlock.setMostSpecificChildren(mostSpecificChildren);


		if (cache != null) {
			log.trace(
					"Hits: {}, Misses: {}, Hits/Misses: {}, %Hits: {} (Up to now)",
					cache.getHits(),
					cache.getMisses(),
					(double) cache.getHits() / cache.getMisses(),
					(double) cache.getHits() / (cache.getHits() + cache.getMisses())
			);
		}
	}

}

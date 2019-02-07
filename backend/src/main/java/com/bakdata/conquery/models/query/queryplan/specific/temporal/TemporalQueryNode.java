package com.bakdata.conquery.models.query.queryplan.specific.temporal;

import java.util.OptionalInt;
import java.util.Set;

import com.bakdata.conquery.models.common.CDateSet;
import com.bakdata.conquery.models.datasets.Table;
import com.bakdata.conquery.models.events.Block;
import com.bakdata.conquery.models.identifiable.ids.specific.TableId;
import com.bakdata.conquery.models.query.QueryContext;
import com.bakdata.conquery.models.query.entity.Entity;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.SpecialDateUnion;

import lombok.Getter;

/**
 * QueryNode implementing the logic for TemporalQueries.
 * Executes two queries and compares the times they are included, the entity is included according to a specified {@link PrecedenceMatcher}.
 */
@Getter
public class TemporalQueryNode extends QPNode {

	/**
	 * Matcher to be used when testing for inclusion.
	 */
	private PrecedenceMatcher matcher;

	/**
	 * QueryPlan for the events to be compared to.
	 */
	private SampledNode reference;

	/**
	 * QueryPlan for the events being compared.
	 */
	private SampledNode preceding;

	/**
	 * The {@link SpecialDateUnion} to be fed with the included dataset.
	 */
	private SpecialDateUnion dateUnion;

	public TemporalQueryNode(SampledNode reference, SampledNode preceding, PrecedenceMatcher matcher, SpecialDateUnion dateUnion) {
		this.reference = reference;
		this.preceding = preceding;
		this.matcher = matcher;
		this.dateUnion = dateUnion;
	}

	@Override
	public QPNode clone(QueryPlan plan, QueryPlan clone) {
		return new TemporalQueryNode(reference.clone(), preceding.clone(), matcher, clone.getIncluded());
	}

	/**
	 * Collects required tables of {@link #reference} and {@link #preceding} into {@code out}.
	 *
	 * @param out the set to be filled with data.
	 */
	@Override
	public void collectRequiredTables(Set<TableId> out) {
		reference.getChild().getRoot().collectRequiredTables(out);
		preceding.getChild().getRoot().collectRequiredTables(out);
	}

	/**
	 * Initializes the {@link TemporalQueryNode} and its children.
	 *
	 * @param entity the Entity to be worked on.
	 */
	@Override
	public void init(Entity entity) {
		super.init(entity);

		reference.getChild().getRoot().init(entity);
		preceding.getChild().getRoot().init(entity);
	}

	/**
	 * Calls nextBlock on its children.
	 * @param block the new Block
	 */
	@Override
	public void nextBlock(Block block) {
		reference.getChild().getRoot().nextBlock(block);
		preceding.getChild().getRoot().nextBlock(block);
	}

	/**
	 * Calls nextBlock on its children.documentation code for refactored matchers.
	 * @param ctx The new QueryContext
	 * @param currentTable the new Table
	 */
	@Override
	public void nextTable(QueryContext ctx, Table currentTable) {
		reference.getChild().getRoot().nextTable(ctx, currentTable);
		preceding.getChild().getRoot().nextTable(ctx, currentTable);
	}

	/**
	 * Delegates aggregation to {@link #reference} and {@link #preceding}.
	 * @param block the specific Block
	 * @param event the event to aggregate over
	 * @return always true.
	 */
	@Override
	public boolean nextEvent(Block block, int event) {
		reference.getChild().getRoot().aggregate(block, event);
		preceding.getChild().getRoot().aggregate(block, event);

		return true;
	}

	/**
	 * Retrieves the {@link QueryPlan#getIncluded()} time of {@link #reference} and {@link #preceding}.
	 * Then tests whether they match the specific criteria for inclusion.
	 * If the criteria are met, the matching {@link CDateSet} is put into the @{@link SpecialDateUnion} node of the Queries associated QueryPlan.
	 *
	 * @return true, iff the Events match the specific criteria.
	 */
	@Override
	public final boolean isContained() {
		if (!(reference.getChild().getRoot().isContained() && preceding.getChild().getRoot().isContained())) {
			return false;
		}

		CDateSet referenceDurations = getReference().getChild().getIncluded().getAggregationResult();
		// Create copy as we are mutating the set
		CDateSet precedingDurations = CDateSet.create(getPreceding().getChild().getIncluded().getAggregationResult());


		OptionalInt sampledReference = getReference().getSampler().sample(referenceDurations);

		if (!sampledReference.isPresent())
			return false;

		matcher.removePreceding(precedingDurations, sampledReference.getAsInt());

		OptionalInt sampledPreceding = getReference().getSampler().sample(precedingDurations);

		if (!precedingDurations.isEmpty() && matcher.isContained(sampledReference, sampledPreceding)) {
			dateUnion.merge(precedingDurations);
			return true;
		}

		return false;
	}
}

package com.bakdata.conquery.models.concepts.select;

import com.bakdata.conquery.io.cps.CPSBase;
import com.bakdata.conquery.models.concepts.Connector;
import com.bakdata.conquery.models.concepts.SelectHolder;
import com.bakdata.conquery.models.externalservice.ResultType;
import com.bakdata.conquery.models.identifiable.Labeled;
import com.bakdata.conquery.models.identifiable.ids.specific.ConceptSelectId;
import com.bakdata.conquery.models.identifiable.ids.specific.ConnectorSelectId;
import com.bakdata.conquery.models.identifiable.ids.specific.SelectId;
import com.bakdata.conquery.models.query.queryplan.aggregators.Aggregator;
import com.bakdata.conquery.models.query.queryplan.specific.AggregatorNode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, property="type")
@CPSBase
public abstract class Select extends Labeled<SelectId> {

	@JsonBackReference @Getter @Setter
	private SelectHolder<?> holder;

	@Setter @Getter
	private String description;

	public AggregatorNode<?> createAggregator(int position) {
		return new AggregatorNode<>(position, createAggregator());
	}

	protected abstract Aggregator<?> createAggregator();

	@Override
	public SelectId createId() {
		if(holder instanceof Connector) {
			return new ConnectorSelectId(((Connector)holder).getId(), getName());
		}
		else
			return new ConceptSelectId(holder.findConcept().getId(), getName());
	}
	
	@JsonIgnore
	public abstract ResultType getResultType();
}

package com.bakdata.conquery.models.query.concept;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.bakdata.conquery.ConqueryConstants;
import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.IQuery;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.QueryResolveContext;
import com.bakdata.conquery.models.query.concept.ResultInfo.ResultInfoCollector;
import com.bakdata.conquery.models.query.queryplan.ConceptQueryPlan;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.visitor.QueryVisitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CPSType(id = "CONCEPT_QUERY", base = IQuery.class)
public class ConceptQuery implements IQuery {

	@Valid
	@NotNull
	protected CQElement root;

	@Override
	public ConceptQueryPlan createQueryPlan(QueryPlanContext context) {
		ConceptQueryPlan qp = new ConceptQueryPlan(context);
		qp.setChild(root.createQueryPlan(context, qp));
		return qp;
	}

	@Override
	public void collectRequiredQueries(Set<ManagedExecutionId> requiredQueries) {
		root.collectRequiredQueries(requiredQueries);
	}

	@Override
	public ConceptQuery resolve(QueryResolveContext context) {
		this.root = root.resolve(context);
		return this;
	}

	public List<SelectDescriptor> collectSelects() {
		return root.collectSelects();
	}
	
	@Override
	public void collectResultInfos(ResultInfoCollector collector) {
		collector.add(ConqueryConstants.DATES_INFO);
		for (SelectDescriptor selectDescriptor : collectSelects()) {
			collector.add(selectDescriptor);
		}
	}

	@Override
	public void visit(QueryVisitor visitor) {
		root.visit(visitor);
	}
}
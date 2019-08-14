package com.bakdata.conquery.models.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bakdata.conquery.io.cps.CPSBase;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.concept.ResultInfo;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.visitor.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, property="type")
@CPSBase
public interface IQuery {

	IQuery resolve(QueryResolveContext context);
	QueryPlan createQueryPlan(QueryPlanContext context);
	
	void collectRequiredQueries(Set<ManagedExecutionId> requiredQueries);
	
	default Set<ManagedExecutionId> collectRequiredQueries() {
		HashSet<ManagedExecutionId> set = new HashSet<>();
		this.collectRequiredQueries(set);
		return set;
	}

	List<ResultInfo> collectResultInfos(PrintSettings config);
	
	void visit(QueryVisitor visitor);
}

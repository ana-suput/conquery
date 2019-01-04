package com.bakdata.conquery.models.query.concept.specific;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedQueryId;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.queryplan.specific.AndNode;

import lombok.Getter;
import lombok.Setter;

@CPSType(id="AND", base=CQElement.class)
public class CQAnd implements CQElement {
	@Getter @Setter @NotEmpty @Valid
	private List<CQElement> children;

	@Override
	public QPNode createQueryPlan(QueryPlanContext context, QueryPlan plan) {
		QPNode[] aggs = new QPNode[children.size()];
		for(int i=0;i<aggs.length;i++) {
			aggs[i] = children.get(i).createQueryPlan(context, plan);
		}
		return new AndNode(Arrays.asList(aggs));
	}
	
	@Override
	public void collectRequiredQueries(Set<ManagedQueryId> requiredQueries) {
		for(CQElement c:children) {
			c.collectRequiredQueries(requiredQueries);
		}
	}
}
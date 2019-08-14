package com.bakdata.conquery.models.query.concept.specific;

import java.util.Deque;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.identifiable.ids.NamespacedId;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.QueryResolveContext;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.concept.SelectDescriptor;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.queryplan.specific.NegatingNode;
import com.bakdata.conquery.models.query.visitor.QueryVisitor;

import lombok.Getter;
import lombok.Setter;

@CPSType(id="NEGATION", base=CQElement.class)
@Setter
@Getter
public class CQNegation implements CQElement {

	@Valid @NotNull @Getter @Setter
	private CQElement child;

	@Override
	public QPNode createQueryPlan(QueryPlanContext context, QueryPlan plan) {
		return new NegatingNode(child.createQueryPlan(context.withGenerateSpecialDateUnion(false), plan));
	}

	@Override
	public CQElement resolve(QueryResolveContext context) {
		child = child.resolve(context);
		return this;
	}
	
	@Override
	public void collectSelects(Deque<SelectDescriptor> select) {
		child.collectSelects(select);
	}

	@Override
	public void collectNamespacedIds(Set<NamespacedId> namespacedIds) {
		child.collectNamespacedIds(namespacedIds);
	}
	
	@Override
	public void visit(QueryVisitor visitor) {
		child.visit(visitor);		
	}
}

package com.bakdata.conquery.models.query.concept.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.externalservice.ResultType;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.QueryResolveContext;
import com.bakdata.conquery.models.query.Visitable;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.queryplan.ConceptQueryPlan;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.ExistsAggregator;
import com.bakdata.conquery.models.query.queryplan.specific.OrNode;
import com.bakdata.conquery.models.query.resultinfo.ResultInfoCollector;
import com.bakdata.conquery.models.query.resultinfo.SimpleResultInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@CPSType(id = "OR", base = CQElement.class)
public class CQOr extends CQElement {
	@Getter
	@Setter
	@NotEmpty
	@Valid
	private List<CQElement> children;

	@Getter
	@Setter
	private boolean summariseExists = false;

	@Override
	public QPNode createQueryPlan(QueryPlanContext context, ConceptQueryPlan plan) {
		QPNode[] nodes = new QPNode[children.size()];

		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = children.get(i).createQueryPlan(context, plan);
		}

		final QPNode or = OrNode.of(Arrays.asList(nodes));

		if (summariseExists) {
			final ExistsAggregator existsAggregator = new ExistsAggregator(or.collectRequiredTables());
			existsAggregator.setReference(or);
			plan.addAggregator(existsAggregator);
		}


		return or;
	}

	@Override
	public void collectRequiredQueries(Set<ManagedExecutionId> requiredQueries) {
		for (CQElement c : children) {
			c.collectRequiredQueries(requiredQueries);
		}
	}

	@Override
	public CQElement resolve(QueryResolveContext context) {
		var copy = new ArrayList<>(children);
		copy.replaceAll(c -> c.resolve(context));
		return new CQOr(copy, isSummariseExists());
	}

	@Override
	public void collectResultInfos(ResultInfoCollector collector) {
		if(summariseExists){
			collector.add(new SimpleResultInfo("OR", ResultType.BOOLEAN));
		}

		for (CQElement c : children) {
			c.collectResultInfos(collector);
		}
	}

	@Override
	public void visit(Consumer<Visitable> visitor) {
		super.visit(visitor);
		for (CQElement c : children) {
			c.visit(visitor);
		}
	}
}

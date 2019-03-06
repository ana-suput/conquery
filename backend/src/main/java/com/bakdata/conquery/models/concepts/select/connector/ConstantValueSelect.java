package com.bakdata.conquery.models.concepts.select.connector;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.concepts.select.Select;
import com.bakdata.conquery.models.externalservice.ResultType;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.ConstantValueAggregator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@CPSType(id = "CONSTANT_VALUE", base = Select.class)
public class ConstantValueSelect extends Select {
	@Getter @Setter
	private String value;
	
	@Override
	protected ConstantValueAggregator createAggregator() {
		return new ConstantValueAggregator(value);
	}

	@Override
	public ResultType getResultType() {
		return ResultType.STRING;
	}
}

package com.bakdata.conquery.models.types.parser.specific;

import static org.assertj.core.api.Assertions.assertThat;

import com.bakdata.conquery.models.config.ParserConfig;
import com.bakdata.conquery.models.events.stores.base.DoubleStore;
import com.bakdata.conquery.models.events.stores.base.FloatStore;
import com.bakdata.conquery.models.types.specific.RealType;
import org.junit.jupiter.api.Test;

class RealParserTest {

	@Test
	public void ulpInRange() {
		final ParserConfig parserConfig = new ParserConfig();
		parserConfig.setMinPrecision(Math.ulp(10));

		final RealParser realParser = new RealParser(parserConfig);

		realParser.registerValue(1d);
		realParser.registerValue(2d);
		realParser.registerValue(-3d);

		// ULP is symmetric on the 0 axis and monotonic, so larger values have a larger ULP

		assertThat(((RealType) realParser.decideType()).getDelegate()).isInstanceOf(FloatStore.class);

		realParser.registerValue(3000d);

		assertThat(((RealType) realParser.decideType()).getDelegate()).isInstanceOf(DoubleStore.class);
	}

}
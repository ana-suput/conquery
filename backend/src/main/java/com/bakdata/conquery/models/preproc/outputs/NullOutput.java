package com.bakdata.conquery.models.preproc.outputs;

import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.events.parser.MajorTypeId;
import com.bakdata.conquery.models.events.parser.Parser;
import com.bakdata.conquery.models.exceptions.ParsingException;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Output a null value.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@CPSType(id="NULL", base= OutputDescription.class)
public class NullOutput extends OutputDescription {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	private MajorTypeId inputType;

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public Output createForHeaders(Object2IntArrayMap<String> headers) {
		return new Output() {
			@Override
			protected Object parseLine(String[] row, Parser type, long sourceLine) throws ParsingException {
				return null;
			}
		};
	}

	@Override
	public MajorTypeId getResultType() {
		return inputType;
	}
}

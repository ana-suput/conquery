package com.bakdata.conquery.models.preproc.outputs;

import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.types.MajorTypeId;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import lombok.Data;

/**
 * Parse column as type.
 */
@Data
@CPSType(id = "COPY", base = OutputDescription.class)
public class CopyOutput extends OutputDescription {

	private static final long serialVersionUID = 1L;

	@NotNull
	private String inputColumn;

	@NotNull
	private MajorTypeId inputType;

	@Override
	public Output createForHeaders(Object2IntArrayMap<String> headers) {
		assertRequiredHeaders(headers, inputColumn);

		int column = headers.getInt(inputColumn);

		return (row, type, sourceLine) -> {
			if (row[column] == null) {
				return null;
			}

			return type.parse(row[column]);
		};
	}

	@Override
	public MajorTypeId getResultType() {
		return inputType;
	}
}

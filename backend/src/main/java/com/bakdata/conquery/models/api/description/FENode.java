package com.bakdata.conquery.models.api.description;

import java.time.LocalDate;
import java.util.List;

import com.bakdata.conquery.models.common.KeyValue;
import com.bakdata.conquery.models.common.Range;
import com.bakdata.conquery.models.identifiable.ids.IId;
import com.bakdata.conquery.models.identifiable.ids.specific.ConceptElementId;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents a concept as it is presented to the front end.
 */
@Data @Builder @SuppressWarnings("rawtypes")
public class FENode {
	private IId<?> parent;
	private String label;
	private String description;
	private Boolean active;
	private ConceptElementId[] children;
	private List<KeyValue> additionalInfos;
	private long matchingEntries;
	private Range<LocalDate> dateRange;
	private List<FETable> tables;
	private Boolean detailsAvailable;
	private boolean codeListResolvable;
}
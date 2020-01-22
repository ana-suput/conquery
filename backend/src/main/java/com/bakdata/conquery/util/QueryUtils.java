package com.bakdata.conquery.util;

import java.util.ArrayList;
import java.util.List;

import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.concept.specific.CQAnd;
import com.bakdata.conquery.models.query.concept.specific.CQExternal;
import com.bakdata.conquery.models.query.concept.specific.CQOr;
import com.bakdata.conquery.models.query.concept.specific.CQReusedQuery;
import com.bakdata.conquery.models.query.visitor.QueryVisitor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryUtils {
	
	/**
	 * Checks if the query requires to resolve external ids.
	 * @return True if a {@link CQExternal} is found.
	 */
	public static class ExternalIdChecker implements QueryVisitor{
		private final List<CQExternal> elements = new ArrayList<>();
		
		@Override
		public void accept(CQElement element) {
			if (element instanceof CQExternal) {	
				elements.add((CQExternal)element);
			}
		}
		
		public boolean resolvesExternalIds() {
			return elements.size() > 0;
		}
	}
	
	/**
	 * Find first and only directly ReusedQuery in the queries tree, and return its Id. ie.: arbirtary CQAnd/CQOr with only them or then a ReusedQuery.
	 *
	 * @return Null if not only a single {@link CQReusedQuery} was found beside {@link CQAnd} / {@link CQOr}.
	 */
	public static class SingleReusedChecker implements QueryVisitor{
		final List<CQReusedQuery> reusedElements = new ArrayList<>();
		private boolean containsOthersElements = false;
		
		@Override
		public void accept(CQElement element) {
			if (element instanceof CQReusedQuery) {	
				reusedElements.add((CQReusedQuery)element);
			}
			else if(element instanceof CQAnd || element instanceof CQOr) {
				// Ignore these elements
			}
			else {
				containsOthersElements = true;
			}
		}
		
		public ManagedExecutionId getOnlyReused(){
			return (reusedElements.size() == 1 && !containsOthersElements)? reusedElements.get(0).getQuery() : null;
		}
	}
}
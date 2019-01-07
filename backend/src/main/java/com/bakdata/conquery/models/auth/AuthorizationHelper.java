package com.bakdata.conquery.models.auth;

import java.util.EnumSet;

import com.bakdata.conquery.models.auth.permissions.Ability;
import com.bakdata.conquery.models.auth.permissions.DatasetPermission;
import com.bakdata.conquery.models.auth.permissions.QueryPermission;
import com.bakdata.conquery.models.auth.subjects.User;
import com.bakdata.conquery.models.identifiable.ids.specific.DatasetId;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedQueryId;

public class AuthorizationHelper {
	
	// Dataset Instances
	public static void authorize(User user, DatasetId dataset, Ability ability) {
		authorize(null, dataset, EnumSet.of(ability));
	}
	
	public static void authorize(User user, DatasetId dataset, EnumSet<Ability> abilities) {
		user.checkPermission(new DatasetPermission(null, abilities, dataset));
	}
	
	// Query Instances
	public static void authorize(User user, ManagedQueryId query, Ability ability) {
		authorize(null, query, EnumSet.of(ability));
	}
	
	public static void authorize(User user, ManagedQueryId query, EnumSet<Ability> abilities) {
		user.checkPermission(new QueryPermission(null, abilities, query));
	}
}

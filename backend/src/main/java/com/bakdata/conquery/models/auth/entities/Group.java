package com.bakdata.conquery.models.auth.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.bakdata.conquery.io.jackson.serializer.MetaIdRefCollection;
import com.bakdata.conquery.io.xodus.MasterMetaStorage;
import com.bakdata.conquery.models.exceptions.JSONException;
import com.bakdata.conquery.models.identifiable.ids.specific.GroupId;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * A group consists of users and permissions. The permissions held by the group
 * are effective for all users in the group. In Conquery, when a user shares a
 * query it is currently shared with all groups a user is in.
 *
 */
public class Group extends PermissionOwner<GroupId> {

	@Getter
	@Setter
	@NonNull
	@NotNull
	@NotEmpty
	private String name;
	@Getter
	@Setter
	@NonNull
	@NotNull
	@NotEmpty
	private String label;

	@MetaIdRefCollection
	private Set<User> members = Collections.synchronizedSet(new HashSet<>());

	public Group(String name, String label) {
		this.name = name;
		this.label = label;
	}

	@Override
	protected void updateStorage(MasterMetaStorage storage) throws JSONException {
		storage.updateGroup(this);

	}

	@Override
	public GroupId createId() {
		return new GroupId(name);
	}

	public void addMember(MasterMetaStorage storage, User user) throws JSONException {
		members.add(user);
		updateStorage(storage);
	}

	public void removeMember(MasterMetaStorage storage, User user) throws JSONException {
		members.remove(user);
		updateStorage(storage);
	}

	public boolean containsMember(User user) {
		return members.contains(user);
	}

	public Set<User> getMembers() {
		return Set.copyOf(members);
	}

}

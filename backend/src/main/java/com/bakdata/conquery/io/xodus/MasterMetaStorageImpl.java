package com.bakdata.conquery.io.xodus;

import java.io.File;
import java.util.Collection;

import javax.validation.Validator;

import com.bakdata.conquery.io.xodus.stores.IdentifiableStore;
import com.bakdata.conquery.io.xodus.stores.KeyIncludingStore;
import com.bakdata.conquery.io.xodus.stores.SingletonStore;
import com.bakdata.conquery.models.auth.permissions.ConqueryPermission;
import com.bakdata.conquery.models.auth.subjects.Mandator;
import com.bakdata.conquery.models.auth.subjects.User;
import com.bakdata.conquery.models.config.StorageConfig;
import com.bakdata.conquery.models.exceptions.JSONException;
import com.bakdata.conquery.models.execution.ManagedExecution;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.identifiable.ids.specific.MandatorId;
import com.bakdata.conquery.models.identifiable.ids.specific.PermissionId;
import com.bakdata.conquery.models.identifiable.ids.specific.UserId;
import com.bakdata.conquery.models.worker.Namespaces;
import com.bakdata.conquery.util.functions.Collector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MasterMetaStorageImpl extends ConqueryStorageImpl implements MasterMetaStorage, ConqueryStorage {
	
	private SingletonStore<Namespaces> meta;
	private IdentifiableStore<ManagedExecution> executions;
	private IdentifiableStore<User> authUser;
	private IdentifiableStore<ConqueryPermission> authPermissions;
	private IdentifiableStore<Mandator> authMandator;
	
	@Getter
	private Namespaces namespaces;

	public MasterMetaStorageImpl(Namespaces namespaces, Validator validator, StorageConfig config) {
		super(
			validator,
			config,
			new File(config.getDirectory(), "meta")
		);
		this.namespaces = namespaces;
	}

	@Override
	protected void createStores(Collector<KeyIncludingStore<?, ?>> collector) {
		this.meta = StoreInfo.NAMESPACES.singleton(this);
		this.executions = StoreInfo.EXECUTIONS.identifiable(this, namespaces);
		
		MasterMetaStorage storage = this;
		this.authMandator = StoreInfo.AUTH_MANDATOR.<Mandator>identifiable(storage);
		this.authUser = StoreInfo.AUTH_USER.<User>identifiable(storage);
		this.authPermissions = StoreInfo.AUTH_PERMISSIONS.<ConqueryPermission>identifiable(storage)
			.onAdd(value->		value.getOwnerId().getOwner(storage).addPermissionLocal(value));
		
		collector
			.collect(meta)
			.collect(authMandator)
			//load users before queries
			.collect(authUser)
			.collect(executions)
			.collect(authPermissions);
	}

	@Override
	public void addExecution(ManagedExecution query) throws JSONException {
		executions.add(query);
	}

	@Override
	public ManagedExecution getExecution(ManagedExecutionId id) {
		return executions.get(id);
	}

	@Override
	public Collection<ManagedExecution> getAllExecutions() {
		return executions.getAll();
	}

	@Override
	public void updateExecution(ManagedExecution query) throws JSONException {
		executions.update(query);
	}

	@Override
	public void removeExecution(ManagedExecutionId id) {
		executions.remove(id);
	}
	
	/*
	@Override
	public Namespaces getMeta() {
		return meta.get();
	}

	@Override
	public void updateMeta(Namespaces meta) throws JSONException {
		this.meta.update(meta);
		//see #147 ?
		/*
		if(blockManager != null) {
			blockManager.init(slaveInfo);
		}
		*/
	//}
	
	public void addPermission(ConqueryPermission permission) throws JSONException {
		authPermissions.add(permission);
	}
	
	public Collection<ConqueryPermission> getAllPermissions() {
		return authPermissions.getAll();
	}
	
	public void removePermission(PermissionId permissionId) {
		authPermissions.remove(permissionId);
	}
	
	public void addUser(User user) throws JSONException {
		authUser.add(user);
	}
	
	public User getUser(UserId userId) {
		return authUser.get(userId);
	}
	
	public Collection<User> getAllUsers() {
		return authUser.getAll();
	}
	
	public void removeUser(UserId userId) {
		authUser.remove(userId);
	}

	public void addMandator(Mandator mandator) throws JSONException {
		authMandator.add(mandator);
	}
	
	public Mandator getMandator(MandatorId mandatorId) {
		return authMandator.get(mandatorId);
	}
	
	@Override
	public Collection<Mandator> getAllMandators() {
		return authMandator.getAll();
	}
	
	public void removeMandator(MandatorId mandatorId)  {
		authMandator.remove(mandatorId);
	}

	@Override
	public void updateUser(User user) throws JSONException {
		authUser.update(user);
	}

	@Override
	public ConqueryPermission getPermission(PermissionId id) {
		return authPermissions.get(id);
	}

	@Override
	public void updateMandator(Mandator mandator) throws JSONException {
		authMandator.update(mandator);
	}
}

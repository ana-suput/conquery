package com.bakdata.conquery.models.types.specific.integer;

import com.bakdata.conquery.models.events.ColumnStore;
import com.bakdata.conquery.models.types.CType;
import com.bakdata.conquery.models.types.MajorTypeId;
import lombok.Getter;

@Getter
public abstract class VarIntType extends CType<Long> {

	public VarIntType() {
		super(MajorTypeId.INTEGER);
	}

	public abstract VarIntType select(int[] starts, int[] ends);

	@Override
	public Long get(int event) {
		return getDelegate().get(event);
	}

	public abstract ColumnStore<Long> getDelegate();

	@Override
	public void set(int event, Long value) {
		getDelegate().set(event, value);
	}

	@Override
	public boolean has(int event) {
		return getDelegate().has(event);
	}

	public abstract int toInt(Long value);

	@Override
	public Integer createScriptValue(Long value) {
		return value.intValue();
	}
}

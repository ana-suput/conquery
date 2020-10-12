package com.bakdata.conquery.models.events.stores;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.events.ColumnStore;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;


@CPSType(id = "BYTES", base = ColumnStore.class)
@Getter
public class ByteStore extends ColumnStoreAdapter<Long, ByteStore> {

	private final byte nullValue;
	private final byte[] values;

	@JsonCreator
	public ByteStore(byte[] values, byte nullValue) {
		this.nullValue = nullValue;
		this.values = values;
	}

	public static ByteStore create(int size) {
		return new ByteStore(new byte[size], Byte.MAX_VALUE);
	}

	@Override
	public void set(int event, Long value) {
		if(value == null){
			values[event] = nullValue;
			return;
		}

		values[event] = value.byteValue();
	}

	@Override
	public boolean has(int event) {
		return values[event] != nullValue;
	}

	@Override
	public Long get(int event) {
		return (long) values[event];
	}
}

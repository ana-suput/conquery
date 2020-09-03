package com.bakdata.conquery.models.events.stores;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegerStore extends ColumnStoreAdapter {

	private final int nullValue;
	private final int[] values;

	@Override
	public boolean has(int event) {
		return values[event] != nullValue;
	}

	@Override
	public long getInteger(int event) {
		return (long) values[event];
	}
}

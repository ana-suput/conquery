package com.bakdata.conquery.models.events;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.jackson.serializer.BucketDeserializer;
import com.bakdata.conquery.io.jackson.serializer.NsIdRef;
import com.bakdata.conquery.models.common.CDateSet;
import com.bakdata.conquery.models.common.daterange.CDateRange;
import com.bakdata.conquery.models.datasets.Column;
import com.bakdata.conquery.models.datasets.Import;
import com.bakdata.conquery.models.identifiable.IdentifiableImpl;
import com.bakdata.conquery.models.identifiable.ids.specific.BucketId;
import com.bakdata.conquery.util.io.SmallIn;
import com.bakdata.conquery.util.io.SmallOut;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.tomgibara.bits.BitStore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Getter @Setter @ToString @JsonDeserialize(using = BucketDeserializer.class)
public abstract class Bucket extends IdentifiableImpl<BucketId> implements Iterable<Integer>, JsonSerializable {

	@Min(0)
	private int bucket;
	@NotNull @NsIdRef
	private Import imp;
	@Min(0)
	private int numberOfEvents;
	private int[] offsets;
	@NotNull @Setter
	protected BitStore nullBits;
	
	public Bucket(int bucket, Import imp, int[] offsets) {
		this.bucket = bucket;
		this.imp = imp;
		this.offsets = offsets;
	}
	
	@Override
	public BucketId createId() {
		return new BucketId(imp.getId(), bucket);
	}
	
	@Override
	public PrimitiveIterator.OfInt iterator() {
		return IntStream
			.range(0,getBucketSize())
			.filter(this::containsLocalEntity)
			.map(this::toGlobal)
			.iterator();
	}

	public abstract void initFields(int numberOfEntities);
	
	public int toLocal(int entity) {
		return entity - getBucketSize()*bucket;
	}
	
	public int toGlobal(int entity) {
		return entity + getBucketSize()*bucket;
	}
	
	public boolean containsLocalEntity(int localEntity) {
		return offsets[localEntity]!=-1;
	}
	
	public int getFirstEventOfLocal(int localEntity) {
		return offsets[localEntity];
	}
	
	public int getLastEventOfLocal(int localEntity) {
		for(localEntity++;localEntity < offsets.length;localEntity++) {
			if(offsets[localEntity]!=-1) {
				return offsets[localEntity];
			}
		}
		return numberOfEvents;
	}
	
	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try (SmallOut output = new SmallOut(baos)){
			writeContent(output);
		}
		byte[] content = baos.toByteArray();
		gen.writeStartObject();
		gen.writeNumberField(Fields.bucket, bucket);
		gen.writeStringField(Fields.imp, imp.getId().toString());
		gen.writeNumberField(Fields.numberOfEvents, numberOfEvents);
		gen.writeFieldName(Fields.offsets);
		gen.writeArray(offsets, 0, offsets.length);
		gen.writeBinaryField("content", content);
		gen.writeEndObject();
	}
	
	public boolean has(int event, Column column) {
		return has(event, column.getPosition());
	}

	public abstract int getBucketSize();
	
	protected abstract boolean has(int event, int columnPosition);

	public abstract int getString(int event, Column column);
	public abstract long getInteger(int event, Column column);
	public abstract boolean getBoolean(int event, Column column);
	public abstract double getReal(int event, Column column);
	public abstract BigDecimal getDecimal(int event, Column column);
	public abstract long getMoney(int event, Column column);
	public abstract int getDate(int event, Column column);
	public abstract CDateRange getDateRange(int event, Column column);
	public abstract Object getRaw(int event, Column column);
	public abstract Object getAsObject(int event, Column column);

	public abstract boolean eventIsContainedIn(int event, Column column, CDateRange dateRange);
	public abstract boolean eventIsContainedIn(int event, Column column, CDateSet dateRanges);
	public abstract CDateRange getAsDateRange(int event, Column currentColumn);

	@Override
	public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		this.serialize(gen, serializers);
	}
	
	public abstract Map<String, Object> calculateMap(int event, Import imp);
	public abstract void writeContent(SmallOut output) throws IOException;

	public abstract void read(SmallIn input) throws IOException;
}
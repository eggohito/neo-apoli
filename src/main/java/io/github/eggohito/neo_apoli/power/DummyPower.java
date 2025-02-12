package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addMetadataFields(instance).apply(instance, DummyPower::new));

	public DummyPower(Metadata metaData) {
		super(metaData);
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.DUMMY;
	}

}

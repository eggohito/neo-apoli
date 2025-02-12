package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, DummyPower::new));
	public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = createCommonPacketCodec((buf, power) -> {}, (buf, metadata) -> new DummyPower(metadata));

	public DummyPower(Metadata metaData) {
		super(metaData);
	}

	@Override
	public PowerType<? extends Power> getType() {
		return PowerTypes.DUMMY;
	}

}

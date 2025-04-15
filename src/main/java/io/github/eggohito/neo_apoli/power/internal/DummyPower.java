package io.github.eggohito.neo_apoli.power.internal;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = createSimpleCodec(DummyPower::new);
	public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = createSimplePacketCodec(DummyPower::new);

	public DummyPower(Properties properties) {
		super(properties);
	}

	@Override
	public Type<? extends Power> getType() {
		return PowerTypes.DUMMY;
	}

}

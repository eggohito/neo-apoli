package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class DummyPower extends Power {

	public static final MapCodec<DummyPower> CODEC = createSimpleCodec(DummyPower::new);
	public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = createSimplePacketCodec(DummyPower::new);

	public DummyPower(Properties properties) {
		super(properties);
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.DUMMY;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl<>(holder, this) {};
	}

}

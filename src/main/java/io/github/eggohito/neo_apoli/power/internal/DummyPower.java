package io.github.eggohito.neo_apoli.power.internal;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;

import java.util.function.UnaryOperator;

public class DummyPower extends Power {

	public static final ContextType CONTEXT_TYPE = createContextType(UnaryOperator.identity());

	public static final MapCodec<DummyPower> CODEC = createSimpleCodec(DummyPower::new);
	public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = createSimplePacketCodec(DummyPower::new);

	public DummyPower(Properties properties) {
		super(properties);
	}

	@Override
	public Type<?> getType() {
		return PowerTypes.DUMMY;
	}

	@Override
	public Impl<?> createImpl(Entity holder) {
		return new Impl<>(holder, this) {};
	}

	@Override
	public ContextType getContextType() {
		return CONTEXT_TYPE;
	}

}

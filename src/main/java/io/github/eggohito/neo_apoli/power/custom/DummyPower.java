package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerSerializers;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.NotNull;

public class DummyPower extends Power {

	public DummyPower(Properties properties) {
		super(properties);
	}

	@Override
	public Power.Serializer<?> getSerializer() {
		return PowerSerializers.DUMMY;
	}

	@Override
	public Power.Type<?> createType(Entity holder) {
		return new Type(holder, this);
	}

	public static final class Serializer implements Power.Serializer<DummyPower> {

		public static final MapCodec<DummyPower> CODEC = createSimpleCodec(DummyPower::new);
		public static final PacketCodec<RegistryByteBuf, DummyPower> PACKET_CODEC = createSimplePacketCodec(DummyPower::new);

		@Override
		public ContextType contextType() {
			return ContextTypes.GENERIC;
		}

		@Override
		public MapCodec<DummyPower> mapCodec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, DummyPower> packetCodec() {
			return PACKET_CODEC;
		}

	}

	public static final class Type extends Power.Type<DummyPower> {

		private Type(@NotNull Entity holder, @NotNull DummyPower power) {
			super(holder, power);
		}

	}

}

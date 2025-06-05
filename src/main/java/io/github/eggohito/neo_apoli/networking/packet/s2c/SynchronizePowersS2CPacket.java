package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Map;
import java.util.function.Function;

public record SynchronizePowersS2CPacket(Map<PowerReference.Power, Power> powers) implements CustomPayload {

	private static final PacketCodec<ByteBuf, PowerReference.Power> VALIDATED_REFERENCE_CODEC = PowerReference.PACKET_CODEC.xmap(
		reference -> {

			if (reference instanceof PowerReference.Power powerReference) {
				return powerReference;
			}

			else {
				throw new IllegalArgumentException("Sub-powers are not allowed!");
			}

		},
		Function.identity()
	);

	private static final PacketCodec<RegistryByteBuf, Map<PowerReference.Power, Power>> ENTRY_CODEC = PacketCodecs.map(
		Object2ObjectOpenHashMap::new,
		VALIDATED_REFERENCE_CODEC,
		Power.BASE_PACKET_CODEC
	);

	public static final Id<SynchronizePowersS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowersS2CPacket> CODEC = ENTRY_CODEC.xmap(SynchronizePowersS2CPacket::new, SynchronizePowersS2CPacket::powers);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}

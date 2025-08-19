package io.github.eggohito.neo_apoli.networking.packet.s2c;

import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SynchronizePowerDataS2CPacket(int entityId, PowerReference powerReference, Dynamic<?> data) implements CustomPayload {

	public static final Id<SynchronizePowerDataS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_power_data"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowerDataS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, SynchronizePowerDataS2CPacket::entityId,
		PowerReference.PACKET_CODEC, SynchronizePowerDataS2CPacket::powerReference,
		NeoApoliPacketCodecs.REGISTRY_PASSTHROUGH, SynchronizePowerDataS2CPacket::data,
		SynchronizePowerDataS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}

package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record MountEntityS2CPacket(int passengerId, int vehicleId, boolean force) implements CustomPayload {

	public static final Id<MountEntityS2CPacket> ID = new Id<>(NeoApoli.id("s2c/mount_entity"));
	public static final PacketCodec<ByteBuf, MountEntityS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, MountEntityS2CPacket::passengerId,
		PacketCodecs.INTEGER, MountEntityS2CPacket::vehicleId,
		PacketCodecs.BOOLEAN, MountEntityS2CPacket::force,
		MountEntityS2CPacket::new
	);

	public MountEntityS2CPacket(@NotNull Entity passenger, @NotNull Entity vehicle, boolean force) {
		this(passenger.getId(), vehicle.getId(), force);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}

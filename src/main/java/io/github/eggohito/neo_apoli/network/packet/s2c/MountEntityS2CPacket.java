package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record MountEntityS2CPacket(int passengerId, int vehicleId, boolean force) implements CustomPacketPayload {

	public static final Type<MountEntityS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/mount_entity"));
	public static final StreamCodec<ByteBuf, MountEntityS2CPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, MountEntityS2CPacket::passengerId,
		ByteBufCodecs.INT, MountEntityS2CPacket::vehicleId,
		ByteBufCodecs.BOOL, MountEntityS2CPacket::force,
		MountEntityS2CPacket::new
	);

	public MountEntityS2CPacket(@NotNull Entity passenger, @NotNull Entity vehicle, boolean force) {
		this(passenger.getId(), vehicle.getId(), force);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}

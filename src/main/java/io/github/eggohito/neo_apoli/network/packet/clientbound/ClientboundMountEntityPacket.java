package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ClientboundMountEntityPacket(int passengerId, int vehicleId, boolean force) implements CustomPacketPayload {

	public static final Type<ClientboundMountEntityPacket> TYPE = new Type<>(NeoApoli.id("clientbound/mount_entity"));
	public static final StreamCodec<ByteBuf, ClientboundMountEntityPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, ClientboundMountEntityPacket::passengerId,
		ByteBufCodecs.INT, ClientboundMountEntityPacket::vehicleId,
		ByteBufCodecs.BOOL, ClientboundMountEntityPacket::force,
		ClientboundMountEntityPacket::new
	);

	public ClientboundMountEntityPacket(@NotNull Entity passenger, @NotNull Entity vehicle, boolean force) {
		this(passenger.getId(), vehicle.getId(), force);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(Level level) {

		Entity passenger = level.getEntity(this.passengerId());
		Entity vehicle = level.getEntity(this.vehicleId());

		if (passenger == null && vehicle == null) {
			NeoApoli.LOGGER.warn("Received packet for mounting unknown passenger to unknown vehicle!");
		}

		else if (passenger == null) {
			NeoApoli.LOGGER.warn("Received packet for mounting unknown passenger to vehicle {}!", vehicle.getName().getString());
		}

		else if (vehicle == null) {
			NeoApoli.LOGGER.warn("Received packet for mounting passenger {} to an unknown vehicle!", passenger.getName().getString());
		}

		else if (passenger.startRiding(vehicle, this.force())) {
			NeoApoli.LOGGER.debug("Passenger {} started riding vehicle {}!", passenger.getName().getString(), vehicle.getName().getString());
		}

		else {
			NeoApoli.LOGGER.warn("Passenger {} failed to start riding vehicle {}!", passenger.getName().getString(), vehicle.getName().getString());
		}

	}

}

package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ClientboundDismountEntityPacket(int passengerId) implements CustomPacketPayload {

	public static final Type<ClientboundDismountEntityPacket> TYPE = new Type<>(NeoApoli.id("clientbound/dismount_entity"));
	public static final StreamCodec<ByteBuf, ClientboundDismountEntityPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, ClientboundDismountEntityPacket::passengerId,
		ClientboundDismountEntityPacket::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(Level level) {

		Entity passenger = level.getEntity(this.passengerId());

		if (passenger != null) {
			passenger.stopRiding();
		}

		else {
			NeoApoli.LOGGER.warn("Received packet for dismounting unknown passenger!");
		}

	}

}

package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SynchronizeDataCommandStorageS2CPacket(Identifier id, NbtCompound nbt) implements CustomPayload {

	public static final Id<SynchronizeDataCommandStorageS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_data_command"));
	public static final PacketCodec<PacketByteBuf, SynchronizeDataCommandStorageS2CPacket> CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, SynchronizeDataCommandStorageS2CPacket::id,
		PacketCodecs.UNLIMITED_NBT_COMPOUND, SynchronizeDataCommandStorageS2CPacket::nbt,
		SynchronizeDataCommandStorageS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}

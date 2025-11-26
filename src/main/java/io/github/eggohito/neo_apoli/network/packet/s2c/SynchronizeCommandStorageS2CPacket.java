package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SynchronizeCommandStorageS2CPacket(ResourceLocation id, CompoundTag nbt) implements CustomPacketPayload {

	public static final Type<SynchronizeCommandStorageS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_data_command"));
	public static final StreamCodec<FriendlyByteBuf, SynchronizeCommandStorageS2CPacket> CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SynchronizeCommandStorageS2CPacket::id,
		ByteBufCodecs.TRUSTED_COMPOUND_TAG, SynchronizeCommandStorageS2CPacket::nbt,
		SynchronizeCommandStorageS2CPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}

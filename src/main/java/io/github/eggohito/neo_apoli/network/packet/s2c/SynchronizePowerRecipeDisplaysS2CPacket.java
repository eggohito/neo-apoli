package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerReference;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SynchronizePowerRecipeDisplaysS2CPacket(Int2ObjectMap<PowerReference> displays) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<PowerReference>> RECIPE_CODEC = ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.INT, PowerReference.STREAM_CODEC);

	public static final Type<SynchronizePowerRecipeDisplaysS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_recipe_displays"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerRecipeDisplaysS2CPacket> CODEC = RECIPE_CODEC.map(SynchronizePowerRecipeDisplaysS2CPacket::new, SynchronizePowerRecipeDisplaysS2CPacket::displays);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}

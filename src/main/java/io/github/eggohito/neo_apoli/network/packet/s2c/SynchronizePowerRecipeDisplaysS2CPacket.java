package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.Map;

public record SynchronizePowerRecipeDisplaysS2CPacket(Map<RecipeDisplayEntry, PowerReference> displays) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<RecipeDisplayEntry, PowerReference>> RECIPE_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, RecipeDisplayEntry.STREAM_CODEC, PowerReference.STREAM_CODEC);

	public static final Type<SynchronizePowerRecipeDisplaysS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_recipe_displays"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerRecipeDisplaysS2CPacket> CODEC = RECIPE_CODEC.map(SynchronizePowerRecipeDisplaysS2CPacket::new, SynchronizePowerRecipeDisplaysS2CPacket::displays);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}

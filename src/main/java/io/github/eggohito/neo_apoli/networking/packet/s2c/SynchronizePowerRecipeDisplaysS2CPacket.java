package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.RecipeDisplayEntry;

import java.util.Map;

public record SynchronizePowerRecipeDisplaysS2CPacket(Map<RecipeDisplayEntry, PowerReference> displays) implements CustomPayload {

	private static final PacketCodec<RegistryByteBuf, Map<RecipeDisplayEntry, PowerReference>> RECIPES_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, RecipeDisplayEntry.PACKET_CODEC, PowerReference.PACKET_CODEC);

	public static final Id<SynchronizePowerRecipeDisplaysS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_power_recipe_displays"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowerRecipeDisplaysS2CPacket> CODEC = RECIPES_PACKET_CODEC.xmap(SynchronizePowerRecipeDisplaysS2CPacket::new, SynchronizePowerRecipeDisplaysS2CPacket::displays);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}

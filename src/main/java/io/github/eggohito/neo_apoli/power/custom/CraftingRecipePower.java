package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowerRecipeDisplaysS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter
public class CraftingRecipePower extends Power implements Prioritized<CraftingRecipePower> {

	public static final MapCodec<CraftingRecipePower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliMapCodecs.CRAFTING_RECIPE_ENTRY.fieldOf("recipe").forGetter(CraftingRecipePower::getRecipeEntry),
		Codec.INT.optionalFieldOf("priority", 0).forGetter(CraftingRecipePower::getPriority)
	).apply(instance, CraftingRecipePower::new));

	public static final PacketCodec<RegistryByteBuf, CraftingRecipePower> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.CRAFTING_RECIPE_ENTRY, CraftingRecipePower::getRecipeEntry,
		PacketCodecs.INTEGER, CraftingRecipePower::getPriority,
		CraftingRecipePower::new
	);

	private final RecipeEntry<CraftingRecipe> recipeEntry;
	private final int priority;

	public CraftingRecipePower(RecipeEntry<CraftingRecipe> recipeEntry, int priority) {
		this.recipeEntry = recipeEntry;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.CRAFTING_RECIPE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<CraftingRecipePower> {

		protected Instance(@NotNull Entity holder, @NotNull CraftingRecipePower power) {
			super(holder, power);
		}

		public RecipeEntry<CraftingRecipe> getRecipeEntry() {
			return this.getPower().getRecipeEntry();
		}

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void resetRecipeDisplays(ClientPlayNetworkHandler ignoredHandler, MinecraftClient client) {
		((PowerRecipeDisplayHolder) client).neo_apoli$setReferencesByDisplayEntry(new Object2ObjectOpenHashMap<>());
	}

	@ApiStatus.Internal
	public static void sendRecipeDisplays(ServerPlayerEntity player, boolean ignoredJoined) {
		ServerRecipeManager recipeManager = player.server.getRecipeManager();
		ServerPlayNetworking.send(player, new SynchronizePowerRecipeDisplaysS2CPacket(((PowerRecipeDisplayHolder) recipeManager).neo_apoli$getReferencesByDisplayEntry()));
	}

}

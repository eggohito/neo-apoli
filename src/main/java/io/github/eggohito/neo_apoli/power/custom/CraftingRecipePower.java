package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerRecipeDisplaysS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class CraftingRecipePower extends Power implements Prioritized<CraftingRecipePower> {

	public static final MapCodec<CraftingRecipePower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliMapCodecs.CRAFTING_RECIPE_ENTRY.fieldOf("recipe").forGetter(CraftingRecipePower::getRecipeEntry),
		Codec.INT.optionalFieldOf("priority", 0).forGetter(CraftingRecipePower::getPriority)
	).apply(instance, CraftingRecipePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CraftingRecipePower> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.CRAFTING_RECIPE_ENTRY, CraftingRecipePower::getRecipeEntry,
		ByteBufCodecs.INT, CraftingRecipePower::getPriority,
		CraftingRecipePower::new
	);

	private final RecipeHolder<CraftingRecipe> recipeEntry;
	private final int priority;

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

		public RecipeHolder<CraftingRecipe> getRecipeEntry() {
			return this.getPower().getRecipeEntry();
		}

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void resetRecipeDisplays(ClientPacketListener ignoredHandler, Minecraft client) {
		((PowerRecipeDisplayHolder) client).neo_apoli$setReferencesByDisplayEntry(new Object2ObjectOpenHashMap<>());
	}

	@ApiStatus.Internal
	public static void sendRecipeDisplays(ServerPlayer player, boolean ignoredJoined) {
		RecipeManager recipeManager = player.server.getRecipeManager();
		ServerPlayNetworking.send(player, new SynchronizePowerRecipeDisplaysS2CPacket(((PowerRecipeDisplayHolder) recipeManager).neo_apoli$getReferencesByDisplayEntry()));
	}

}

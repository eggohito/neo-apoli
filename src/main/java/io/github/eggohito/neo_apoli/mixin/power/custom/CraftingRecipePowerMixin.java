package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.duck.PowerCraftingInventory;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerRecipeDisplaysS2CPacket;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.recipe.PowerCraftingRecipe;
import io.github.eggohito.neo_apoli.recipe.PowerStackedItemContents;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

public abstract class CraftingRecipePowerMixin {

	@Mixin(RecipeManager.class)
	public static abstract class ManagerRegistrant implements PowerRecipeDisplayHolder {

		@Shadow
		private RecipeMap recipes;

		@Shadow
		private Map<ResourceKey<Recipe<?>>, List<RecipeManager.ServerDisplayInfo>> recipeToDisplay;

		@Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
		private void onFinalizeLoad(FeatureFlagSet features, CallbackInfo ci) {

			ObjectCollection<RecipeHolder<?>> recipeEntries = new ObjectOpenHashSet<>(this.recipes.values());
			Object2IntMap<ResourceKey<Recipe<?>>> replacedRecipes = Util.make(new Object2IntOpenHashMap<>(), map -> recipeEntries.forEach(recipeEntry -> map.put(recipeEntry.id(), 0)));

			for (PowerHolder<?> powerHolder : PowerManager.powers()) {

				if (!(powerHolder.value() instanceof CraftingRecipePower craftingRecipePower)) {
					continue;
				}

				RecipeHolder<CraftingRecipe> recipeEntry = craftingRecipePower.getRecipeEntry();
				ResourceKey<Recipe<?>> recipeKey = recipeEntry.id();

				CraftingRecipe recipe = recipeEntry.value();
				int priority = craftingRecipePower.getPriority();

				if (!replacedRecipes.containsKey(recipeKey) || priority > replacedRecipes.getInt(recipeKey)) {

					var replacement = new RecipeHolder<>(recipeKey, new PowerCraftingRecipe(powerHolder.id(), recipe));

					recipeEntries.remove(recipeEntry);
					recipeEntries.add(replacement);

				}

				replacedRecipes.put(recipeKey, priority);

			}

			this.recipes = RecipeMap.create(recipeEntries);

		}

		@Unique
		private final Int2ObjectMap<PowerIdentifier> neo_apoli$powerIdsByIndex = new Int2ObjectOpenHashMap<>();

		@Inject(method = "finalizeRecipeLoading", at = @At("TAIL"))
		private void afterFinalizeLoad(FeatureFlagSet features, CallbackInfo ci) {

			this.neo_apoli$powerIdsByIndex.clear();
			this.recipeToDisplay.forEach((key, serverRecipes) -> serverRecipes.forEach(serverRecipe -> {

				if (serverRecipe.parent().value() instanceof PowerCraftingRecipe(PowerIdentifier id, CraftingRecipe ignored)) {
					neo_apoli$powerIdsByIndex.put(serverRecipe.display().id().index(), id);
				}

			}));

		}

		@Override
		public Int2ObjectMap<PowerIdentifier> neo_apoli$getPowerIdsByIndex() {
			return new Int2ObjectOpenHashMap<>(this.neo_apoli$powerIdsByIndex);
		}

		@Override
		public void neo_apoli$sendAll(ServerPlayer recipient) {

			SynchronizePowerRecipeDisplaysS2CPacket packet = new SynchronizePowerRecipeDisplaysS2CPacket(this.neo_apoli$getPowerIdsByIndex());

			if (ServerPlayNetworking.canSend(recipient, packet.type())) {
				ServerPlayNetworking.send(recipient, packet);
			}

		}

	}

	@Mixin(CraftingInput.class)
	public static abstract class CraftingInputCache implements PowerCraftingInventory {

		@Unique
		private final ThreadLocal<TransientCraftingContainer> neo_apoli$inventory = new ThreadLocal<>();

		@Unique
		private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

		@Override
		public TransientCraftingContainer neo_apoli$getInventory() {
			return this.neo_apoli$inventory.get();
		}

		@Override
		public void neo_apoli$setInventory(TransientCraftingContainer inventory) {

			if (inventory == null) {
				this.neo_apoli$inventory.remove();
			}

			else {
				this.neo_apoli$inventory.set(inventory);
			}

		}

		@Override
		public @Nullable Entity neo_apoli$getEntity() {
			return this.neo_apoli$entity.get();
		}

		@Override
		public void neo_apoli$setEntity(@Nullable Entity entity) {

			if (entity == null) {
				this.neo_apoli$entity.remove();
			}

			else {
				this.neo_apoli$entity.set(entity);
			}

		}

	}

	@Mixin(CraftingContainer.class)
	public interface CraftingContainerCache extends PowerCraftingInventory {

		@ModifyReturnValue(method = "asPositionedCraftInput", at = @At("RETURN"))
		private CraftingInput.Positioned passCacheToPositioned(CraftingInput.Positioned original) {

			if (original.input() instanceof PowerCraftingInventory newPci) {
				newPci.neo_apoli$setInventory(this.neo_apoli$getInventory());
				newPci.neo_apoli$setEntity(this.neo_apoli$getEntity());
			}

			return original;

		}

	}

	@Mixin(TransientCraftingContainer.class)
	public static abstract class TransientCraftingContainerCache implements PowerCraftingInventory {

		@Unique
		private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

		@Override
		public TransientCraftingContainer neo_apoli$getInventory() {
			return (TransientCraftingContainer) (Object) this;
		}

		@Override
		public @Nullable Entity neo_apoli$getEntity() {
			return this.neo_apoli$entity.get();
		}

		@Override
		public void neo_apoli$setEntity(@Nullable Entity entity) {

			if (entity == null) {
				this.neo_apoli$entity.remove();
			}

			else {
				this.neo_apoli$entity.set(entity);
			}

		}

	}

	@Mixin(AbstractCraftingMenu.class)
	public static abstract class AbstractCraftingMenuCacheInitializer {

		@Shadow @Final
		protected CraftingContainer craftSlots;

		@Inject(method = "addResultSlot", at = @At("TAIL"))
		private void cachePlayerWhenAddingResultSlot(Player player, int x, int y, CallbackInfoReturnable<Slot> cir) {

			if (this.craftSlots instanceof PowerCraftingInventory powerCraftingInventory) {
				powerCraftingInventory.neo_apoli$setEntity(player);
			}

		}

	}

	@Mixin(RecipeDisplayEntry.class)
	public static abstract class RecipeDisplayCraftableProxy {

		@Shadow
		public abstract RecipeDisplayId id();

		@WrapOperation(method = "canCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/StackedItemContents;canCraft(Ljava/util/List;Lnet/minecraft/world/entity/player/StackedContents$Output;)Z"))
		private boolean accountForPowerCraftingRecipeDisplays(StackedItemContents contents, List<? extends StackedContents.IngredientInfo<Holder<Item>>> rawIngredients, StackedContents.@Nullable Output<Holder<Item>> itemCallback, Operation<Boolean> original) {

			if (contents instanceof PowerStackedItemContents powerContents) {
				return powerContents.isCraftable(this.id(), rawIngredients, 1, itemCallback);
			}

			else {
				return original.call(contents, rawIngredients, itemCallback);
			}

		}

	}

}

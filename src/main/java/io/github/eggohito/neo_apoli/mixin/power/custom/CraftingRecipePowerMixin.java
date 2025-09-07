package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.duck.PowerCraftingInventory;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.recipe.PowerCraftingRecipe;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public abstract class CraftingRecipePowerMixin {

	@Mixin(ServerRecipeManager.class)
	public static abstract class ManagerRegistrant {

		@Shadow
		@Final
		private static Logger LOGGER;

		@Shadow
		private PreparedRecipes preparedRecipes;

		@WrapWithCondition(method = "method_64689", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
		private static <E> boolean validateRecipeEntries(List<E> list, E e, List<E> ignored, Identifier id, Recipe<?> recipe) {
			return RecipeUtil.validateRecipe(recipe)
				.ifError(error -> LOGGER.error("Couldn't register recipe \"{}\": {}", id, error.message()))
				.isSuccess();
		}

		@Inject(method = "initialize", at = @At("HEAD"))
		private void onInit(FeatureSet features, CallbackInfo ci) {

			ObjectCollection<RecipeEntry<?>> recipeEntries = new ObjectOpenHashSet<>(this.preparedRecipes.recipes());
			Object2IntMap<RegistryKey<Recipe<?>>> replacedRecipes = Util.make(new Object2IntOpenHashMap<>(), map -> recipeEntries.forEach(recipeEntry -> map.put(recipeEntry.id(), 0)));

			for (PowerEntry<?> powerEntry: PowerManager.entries()) {

				if (!(powerEntry.value() instanceof CraftingRecipePower craftingRecipePower)) {
					continue;
				}

				RecipeEntry<CraftingRecipe> recipeEntry = craftingRecipePower.getRecipeEntry();
				RegistryKey<Recipe<?>> recipeKey = recipeEntry.id();

				CraftingRecipe recipe = recipeEntry.value();
				int priority = craftingRecipePower.getPriority();

				if (!replacedRecipes.containsKey(recipeKey) || replacedRecipes.getInt(recipeKey) < priority) {

					RecipeEntry<PowerCraftingRecipe> replacement = new RecipeEntry<>(recipeKey, new PowerCraftingRecipe(powerEntry.reference(), recipe));

					recipeEntries.remove(recipeEntry);
					recipeEntries.add(replacement);

				}

				replacedRecipes.put(recipeKey, priority);

			}

			this.preparedRecipes = PreparedRecipes.of(recipeEntries);

		}

	}

	@Mixin(CraftingRecipeInput.class)
	public static abstract class CraftingRecipeInputCache implements PowerCraftingInventory {

		@Unique
		private final ThreadLocal<CraftingInventory> neo_apoli$inventory = new ThreadLocal<>();

		@Unique
		private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

		@Override
		public CraftingInventory neo_apoli$getInventory() {
			return this.neo_apoli$inventory.get();
		}

		@Override
		public void neo_apoli$setInventory(CraftingInventory inventory) {

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

	@Mixin(RecipeInputInventory.class)
	public interface RecipeInputInventoryCache extends PowerCraftingInventory {

		@ModifyReturnValue(method = "createPositionedRecipeInput", at = @At("RETURN"))
		private CraftingRecipeInput.Positioned passCacheToPositioned(CraftingRecipeInput.Positioned original) {

			if (original.input() instanceof PowerCraftingInventory newPci) {
				newPci.neo_apoli$setInventory(this.neo_apoli$getInventory());
				newPci.neo_apoli$setEntity(this.neo_apoli$getEntity());
			}

			return original;

		}

	}

	@Mixin(CraftingInventory.class)
	public static abstract class CraftingInventoryCache implements PowerCraftingInventory {

		@Unique
		private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

		@Override
		public CraftingInventory neo_apoli$getInventory() {
			return (CraftingInventory) (Object) this;
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

	@Mixin(PlayerScreenHandler.class)
	public static abstract class PlayerScreenHandlerCacheInitializer extends AbstractCraftingScreenHandler {

		private PlayerScreenHandlerCacheInitializer(ScreenHandlerType<?> type, int syncId, int width, int height) {
			super(type, syncId, width, height);
		}

		@Inject(method = "<init>", at = @At("TAIL"))
		private void cachePlayerToInventory(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {

			if (this.craftingInventory instanceof PowerCraftingInventory powerCraftingInventory) {
				powerCraftingInventory.neo_apoli$setEntity(owner);
			}

		}

	}

	@Mixin(CraftingScreenHandler.class)
	public static abstract class CraftingScreenHandlerCacheInitializer extends AbstractCraftingScreenHandler {

		private CraftingScreenHandlerCacheInitializer(ScreenHandlerType<?> type, int syncId, int width, int height) {
			super(type, syncId, width, height);
		}

		@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
		private void cachePlayerToInventory(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {

			if (this.craftingInventory instanceof PowerCraftingInventory powerCraftingInventory) {
				powerCraftingInventory.neo_apoli$setEntity(playerInventory.player);
			}

		}

	}

}

package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.duck.PowerCraftingInventory;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.recipe.PowerCraftingRecipe;
import io.github.eggohito.neo_apoli.recipe.PowerRecipeFinder;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

public abstract class CraftingRecipePowerMixin {

	@Mixin(ServerRecipeManager.class)
	public static abstract class ManagerRegistrant implements PowerRecipeDisplayHolder {

		@Shadow
		@Final
		private static Logger LOGGER;

		@Shadow
		private PreparedRecipes preparedRecipes;

		@Shadow
		private Map<RegistryKey<Recipe<?>>, List<ServerRecipeManager.ServerRecipe>> recipesByKey;

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

		@Unique
		private final Object2ObjectOpenHashMap<RecipeDisplayEntry, PowerReference> neo_apoli$referencesByDisplayEntry = new Object2ObjectOpenHashMap<>();

		@Inject(method = "initialize", at = @At("TAIL"))
		private void afterInit(FeatureSet features, CallbackInfo ci) {

			this.neo_apoli$referencesByDisplayEntry.clear();
			this.recipesByKey.forEach((key, serverRecipes) -> serverRecipes.forEach(serverRecipe -> {

				if (serverRecipe.parent().value() instanceof PowerCraftingRecipe(PowerReference powerReference, CraftingRecipe ignoredDelegate)) {
					neo_apoli$referencesByDisplayEntry.put(serverRecipe.display(), powerReference);
				}

			}));

		}

		@Override
		public Map<RecipeDisplayEntry, PowerReference> neo_apoli$getReferencesByDisplayEntry() {
			return new Object2ObjectOpenHashMap<>(this.neo_apoli$referencesByDisplayEntry);
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

	@Mixin(AbstractCraftingScreenHandler.class)
	public static abstract class AbstractCraftingScreenCacheInitializer {

		@Shadow @Final
		protected RecipeInputInventory craftingInventory;

		@Inject(method = "addResultSlot", at = @At("TAIL"))
		private void cachePlayerWhenAddingResultSlot(PlayerEntity player, int x, int y, CallbackInfoReturnable<Slot> cir) {

			if (this.craftingInventory instanceof PowerCraftingInventory powerCraftingInventory) {
				powerCraftingInventory.neo_apoli$setEntity(player);
			}

		}

	}

	@Mixin(RecipeDisplayEntry.class)
	public static abstract class RecipeDisplayCraftableProxy {

		@Shadow
		public abstract NetworkRecipeId id();

		@WrapOperation(method = "isCraftable", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeFinder;isCraftable(Ljava/util/List;Lnet/minecraft/recipe/RecipeMatcher$ItemCallback;)Z"))
		private boolean accountForPowerCraftingRecipeDisplays(RecipeFinder finder, List<? extends RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients, RecipeMatcher.@Nullable ItemCallback<RegistryEntry<Item>> itemCallback, Operation<Boolean> original) {

			if (finder instanceof PowerRecipeFinder powerRecipeFinder) {
				return powerRecipeFinder.isCraftable(this.id(), rawIngredients, 1, itemCallback);
			}

			else {
				return original.call(finder, rawIngredients, itemCallback);
			}

		}

	}

}

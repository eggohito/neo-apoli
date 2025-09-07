package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.recipe.PowerCraftingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;

public class RecipeUtil {

	public static <R extends Recipe<?>> DataResult<R> validateRecipe(@NotNull R recipe) {
		return switch (recipe) {
			case PowerCraftingRecipe powerCraftingRecipe ->
				createInternalOnlyError(powerCraftingRecipe.getSerializer());
			default ->
				DataResult.success(recipe);
		};
	}

	public static <R extends Recipe<?>> DataResult<CraftingRecipe> validateCraftingRecipe(@NotNull R recipe) {
		return validateRecipe(recipe).flatMap(innerRecipe -> {

			if (innerRecipe instanceof CraftingRecipe craftingRecipe) {
				return DataResult.success(craftingRecipe);
			}

			else {
				return DataResult.error(() -> "Recipe is not a crafting recipe!");
			}

		});
	}

	private static <R> DataResult<R> createInternalOnlyError(RecipeSerializer<?> serializer) {
		return DataResult.error(() -> "Recipe type \"" + RegistryUtil.getId(Registries.RECIPE_SERIALIZER, serializer) + "\" is for internal use only!");
	}

}

package io.github.eggohito.neo_apoli.recipe;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NeoApoliRecipeSerializers {

	public static final PowerCraftingRecipe.Serializer POWER_CRAFTING = registerInternal("power_crafting", new PowerCraftingRecipe.Serializer());

	public static void registerAll() {

	}

	private static <R extends Recipe<?>, S extends RecipeSerializer<R>> S registerInternal(String path, S serializer) {
		return register(NeoApoli.id(path), serializer);
	}

	public static <R extends Recipe<?>, S extends RecipeSerializer<R>> S register(Identifier id, S serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
	}

}

package io.github.eggohito.neo_apoli.registry.recipe;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.recipe.PowerCraftingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class NeoApoliRecipeSerializers {

	public static final PowerCraftingRecipe.Serializer POWER_CRAFTING = registerInternal("power/crafting", new PowerCraftingRecipe.Serializer());

	public static void registerAll() {

	}

	private static <R extends Recipe<?>, S extends RecipeSerializer<R>> S registerInternal(String path, S serializer) {
		return register(NeoApoli.id(path), serializer);
	}

	public static <R extends Recipe<?>, S extends RecipeSerializer<R>> S register(ResourceLocation id, S serializer) {
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
	}

}

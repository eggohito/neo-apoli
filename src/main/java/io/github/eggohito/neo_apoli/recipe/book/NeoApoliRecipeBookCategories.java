package io.github.eggohito.neo_apoli.recipe.book;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class NeoApoliRecipeBookCategories {

	public static final RecipeBookCategory POWER_CRAFTING_RECIPE = registerInternal("power/crafting_recipe");

	public static void registerAll() {

	}

	private static RecipeBookCategory registerInternal(String path) {
		return register(NeoApoli.id(path), new RecipeBookCategory() {

			@Override
			public String toString() {
				return path.toUpperCase(Locale.ROOT);
			}

		});
	}

	public static <C extends RecipeBookCategory> C register(Identifier id, C category) {
		return Registry.register(Registries.RECIPE_BOOK_CATEGORY, id, category);
	}

}

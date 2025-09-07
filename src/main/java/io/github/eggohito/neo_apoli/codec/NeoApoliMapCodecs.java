package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;

public class NeoApoliMapCodecs {

	public static final MapCodec<Vec3d> VECTOR_3_DOUBLE = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(Vec3d::getX),
		Codec.DOUBLE.fieldOf("y").forGetter(Vec3d::getY),
		Codec.DOUBLE.fieldOf("z").forGetter(Vec3d::getZ)
	).apply(instance, Vec3d::new));

	public static final MapCodec<Recipe<?>> RECIPE = Registries.RECIPE_SERIALIZER.getCodec().dispatchMap(Recipe::getSerializer, RecipeSerializer::codec);

	public static final MapCodec<CraftingRecipe> CRAFTING_RECIPE = RECIPE.flatXmap(RecipeUtil::validateCraftingRecipe, DataResult::success);

	public static final MapCodec<RecipeEntry<?>> RECIPE_ENTRY = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Recipe.KEY_CODEC.fieldOf("id").forGetter(RecipeEntry::id),
		RECIPE.forGetter(RecipeEntry::value)
	).apply(instance, RecipeEntry::new));

	public static final MapCodec<RecipeEntry<CraftingRecipe>> CRAFTING_RECIPE_ENTRY = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Recipe.KEY_CODEC.fieldOf("id").forGetter(RecipeEntry::id),
		CRAFTING_RECIPE.forGetter(RecipeEntry::value)
	).apply(instance, RecipeEntry::new));

}

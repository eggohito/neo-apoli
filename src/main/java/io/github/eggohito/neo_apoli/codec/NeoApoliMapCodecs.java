package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.phys.Vec3;

public class NeoApoliMapCodecs {

	public static final MapCodec<Vec3> VECTOR_3_DOUBLE = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
		Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
		Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
	).apply(instance, Vec3::new));

	public static final MapCodec<Recipe<?>> RECIPE = BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().dispatchMap(Recipe::getSerializer, RecipeSerializer::codec);

	public static final MapCodec<CraftingRecipe> CRAFTING_RECIPE = RECIPE.flatXmap(RecipeUtil::validateCraftingRecipe, DataResult::success);

	public static final MapCodec<RecipeHolder<?>> RECIPE_ENTRY = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Recipe.KEY_CODEC.fieldOf("id").forGetter(RecipeHolder::id),
		RECIPE.forGetter(RecipeHolder::value)
	).apply(instance, RecipeHolder::new));

	public static final MapCodec<RecipeHolder<CraftingRecipe>> CRAFTING_RECIPE_ENTRY = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Recipe.KEY_CODEC.fieldOf("id").forGetter(RecipeHolder::id),
		CRAFTING_RECIPE.forGetter(RecipeHolder::value)
	).apply(instance, RecipeHolder::new));

	public static final MapCodec<CompoundTag> COMPOUND_TAG = MapCodec.assumeMapUnsafe(CompoundTag.CODEC);

}

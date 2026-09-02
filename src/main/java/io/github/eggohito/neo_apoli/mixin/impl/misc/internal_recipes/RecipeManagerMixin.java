package io.github.eggohito.neo_apoli.mixin.impl.misc.internal_recipes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

	@WrapOperation(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"))
	RecipeMap filterInternalRecipeTypes(Iterable<RecipeHolder<?>> recipes, Operation<RecipeMap> original) {

		List<RecipeHolder<?>> filtered = new ObjectArrayList<>();

		for (var recipe : recipes) {
			RecipeUtil.validateRecipe(recipe.value())
				.ifSuccess(ignored -> filtered.add(recipe))
				.ifError(error -> NeoApoli.LOGGER.error("Couldn't register recipe \"{}\": {}", recipe.id(), error.message()));
		}

		return original.call(filtered);

	}

}

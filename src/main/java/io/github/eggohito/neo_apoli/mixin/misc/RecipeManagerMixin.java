package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@WrapWithCondition(method = "method_64689", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private static <E> boolean filterInternalRecipeTypes(List<E> list, E element, List<E> mList, ResourceLocation mId, Recipe<?> mRecipe) {
		return RecipeUtil.validateRecipe(mRecipe)
			.ifError(error -> LOGGER.error("Couldn't register recipe \"{}\": {}", mId, error.message()))
			.isSuccess();
	}

}

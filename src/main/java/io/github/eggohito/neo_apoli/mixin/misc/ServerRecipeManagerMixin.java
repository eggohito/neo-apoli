package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerRecipeManager.class)
public abstract class ServerRecipeManagerMixin {

	@Shadow
	@Final
	private static Logger LOGGER;

	@WrapWithCondition(method = "method_64689", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private static <E> boolean filterInternalRecipeTypes(List<E> list, E element, List<E> mList, Identifier mId, Recipe<?> mRecipe) {
		return RecipeUtil.validateRecipe(mRecipe)
			.ifError(error -> LOGGER.error("Couldn't register recipe \"{}\": {}", mId, error.message()))
			.isSuccess();
	}

}

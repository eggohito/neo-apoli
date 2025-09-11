package io.github.eggohito.neo_apoli.client.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookResults.class)
public interface RecipeBookResultsAccessor {

	@Accessor
	MinecraftClient getClient();

}

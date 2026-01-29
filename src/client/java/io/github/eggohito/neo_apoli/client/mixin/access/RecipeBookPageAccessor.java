package io.github.eggohito.neo_apoli.client.mixin.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookPage.class)
public interface RecipeBookPageAccessor {

	@Accessor
	Minecraft getMinecraft();

}

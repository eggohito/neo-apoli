package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.crafting_recipe;

import com.google.common.collect.ImmutableList;
import io.github.eggohito.neo_apoli.duck.internal.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.recipe.PowerStackedItemContents;
import io.github.eggohito.neo_apoli.registry.recipe.NeoApoliRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(CraftingRecipeBookComponent.class)
public abstract class CraftingRecipeBookComponentMixin extends RecipeBookComponent<AbstractCraftingMenu> {

	@Mutable
	@Shadow
	@Final
	private static List<TabInfo> TABS;

	CraftingRecipeBookComponentMixin(AbstractCraftingMenu craftingScreenHandler, List<TabInfo> tabs) {
		super(craftingScreenHandler, tabs);
	}

	@ModifyArg(method = "selectMatchingRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeCollection;selectRecipes(Lnet/minecraft/world/entity/player/StackedItemContents;Ljava/util/function/Predicate;)V"))
	StackedItemContents overrideRecipeFinder(StackedItemContents original) {
		return new PowerStackedItemContents(this.minecraft.player, original, ((PowerRecipeDisplayHolder) this.minecraft));
	}

	//	Adds a new tab to the recipe book used for storing recipes added by powers
	static {
		TABS = ImmutableList.<TabInfo>builder()
			.addAll(TABS)
			.add(new TabInfo(Items.COMMAND_BLOCK, NeoApoliRecipeBookCategories.POWER_CRAFTING_RECIPE))
			.build();
	}

}

package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_recipe_crafting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyRecipeCraftingPower;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends AbstractCraftingMenu {

	CraftingMenuMixin(MenuType<?> menuType, int containerId, int width, int height) {
		super(menuType, containerId, width, height);
	}

	@ModifyExpressionValue(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CraftingRecipe;assemble(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
	private static ItemStack modifyOnCraft(ItemStack original, AbstractContainerMenu menu, ServerLevel level, Player player, CraftingContainer craftingSlots, @Local(ordinal = 1) RecipeHolder<CraftingRecipe> recipeHolder) {
		return ModifyRecipeCraftingPower.modifyWhenCrafted(player, recipeHolder, original, menu, craftingSlots);
	}

	@ModifyExpressionValue(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"))
	ItemStack modifyOnQuickTake(ItemStack original, Player player, @Local Slot slot) {

		//  Only modify the item when the selected slot is a crafting result slot
		if (slot instanceof ResultSlot) {
			return ModifyRecipeCraftingPower.modifyWhenTaken(player, original, this.craftSlots);
		}

		else {
			return original;
		}

	}

}

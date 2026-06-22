package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_recipe_crafting;

import io.github.eggohito.neo_apoli.power.custom.ModifyRecipeCraftingPower;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin extends Slot {

	@Shadow
	@Final
	private CraftingContainer craftSlots;

	ResultSlotMixin(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@ModifyVariable(method = "onTake", at = @At("HEAD"), argsOnly = true)
	ItemStack modifyOnTake(ItemStack original, Player player) {

		//  Only modify the item when the item is NOT empty (indicating it wasn't quick-moved)
		if (!original.isEmpty()) {
			return ModifyRecipeCraftingPower.modifyWhenTaken(player, original, this.craftSlots);
		}

		else {
			return original;
		}

	}

}

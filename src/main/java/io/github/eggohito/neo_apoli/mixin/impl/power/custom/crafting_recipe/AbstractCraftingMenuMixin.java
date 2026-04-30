package io.github.eggohito.neo_apoli.mixin.impl.power.custom.crafting_recipe;

import io.github.eggohito.neo_apoli.impl.misc.PowerCraftingInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCraftingMenu.class)
public abstract class AbstractCraftingMenuMixin {

	@Shadow
	@Final
	protected CraftingContainer craftSlots;

	@Inject(method = "addResultSlot", at = @At("TAIL"))
	void cachePlayerWhenAddingResultSlot(Player player, int x, int y, CallbackInfoReturnable<Slot> cir) {

		if (this.craftSlots instanceof PowerCraftingInventory powerCraftingInventory) {
			powerCraftingInventory.neo_apoli$setEntity(player);
		}

	}

}

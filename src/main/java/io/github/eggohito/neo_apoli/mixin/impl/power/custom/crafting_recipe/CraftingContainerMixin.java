package io.github.eggohito.neo_apoli.mixin.impl.power.custom.crafting_recipe;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.impl.misc.PowerCraftingInventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingContainer.class)
public interface CraftingContainerMixin extends PowerCraftingInventory {

	@ModifyReturnValue(method = "asPositionedCraftInput", at = @At("RETURN"))
	private CraftingInput.Positioned passCacheToPositioned(CraftingInput.Positioned original) {

		if (original.input() instanceof PowerCraftingInventory newPci) {
			newPci.neo_apoli$setInventory(this.neo_apoli$getInventory());
			newPci.neo_apoli$setEntity(this.neo_apoli$getEntity());
		}

		return original;

	}

}

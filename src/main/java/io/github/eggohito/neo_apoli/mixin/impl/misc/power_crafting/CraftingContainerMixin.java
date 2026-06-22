package io.github.eggohito.neo_apoli.mixin.impl.misc.power_crafting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.impl.misc.PowerCrafting;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingContainer.class)
public interface CraftingContainerMixin extends PowerCrafting {

	@ModifyReturnValue(method = "asPositionedCraftInput", at = @At("RETURN"))
	private CraftingInput.Positioned passCacheToPositioned(CraftingInput.Positioned original) {

		if (original.input() instanceof PowerCrafting powerCrafting) {
			powerCrafting.neo_apoli$setModifyingInstances(this.neo_apoli$getModifyingInstances());
			powerCrafting.neo_apoli$setInventory(this.neo_apoli$getInventory());
			powerCrafting.neo_apoli$setEntity(this.neo_apoli$getEntity());
		}

		return original;

	}

}

package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_item_wearable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {

	@WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EquipmentSlot$Type;HUMANOID_ARMOR:Lnet/minecraft/world/entity/EquipmentSlot$Type;", opcode = Opcodes.GETSTATIC)))
	boolean onArmorSlotOccupancyCheck(Slot slot, Operation<Boolean> original, @Local(ordinal = 1) ItemStack movingStack) {
		return original.call(slot)
			|| !slot.mayPlace(movingStack);
	}

}

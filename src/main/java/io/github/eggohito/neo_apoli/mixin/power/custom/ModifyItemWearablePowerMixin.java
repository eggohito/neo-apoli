package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyItemWearablePower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

public abstract class ModifyItemWearablePowerMixin {

	@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
	public static abstract class InsertingToArmorSlot {

		@WrapOperation(method = "mayPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEquippableInSlot(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z"))
		private boolean onInsert(LivingEntity owner, ItemStack stack, EquipmentSlot slot, Operation<Boolean> original) {

			List<ModifyItemWearablePower.Instance> instances = PowersComponent.getInstances(owner, ModifyItemWearablePower.Instance.class);
			Context context = ModifyItemWearablePower.createContext(owner, stack);

			return ModifyItemWearablePower.modify(context, instances, slot, () -> original.call(owner, stack, slot));

		}

	}

	@Mixin(InventoryMenu.class)
	public static abstract class QuickInserting {

		@WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EquipmentSlot$Type;HUMANOID_ARMOR:Lnet/minecraft/world/entity/EquipmentSlot$Type;", opcode = Opcodes.GETSTATIC)))
		private boolean onArmorSlotOccupancyCheck(Slot slot, Operation<Boolean> original, @Local(ordinal = 1) ItemStack movingStack) {
			return original.call(slot)
				 || slot.mayPlace(movingStack);
		}

	}

	@Mixin(Equippable.class)
	public static abstract class SwapEquipping {

		@Shadow
		public abstract EquipmentSlot slot();

		@WrapOperation(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/equipment/Equippable;canBeEquippedBy(Lnet/minecraft/world/entity/EntityType;)Z"))
		private boolean onSwap(Equippable equippable, EntityType<?> entityType, Operation<Boolean> original, ItemStack stack, Player player) {

			List<ModifyItemWearablePower.Instance> instances = PowersComponent.getInstances(player, ModifyItemWearablePower.Instance.class);
			Context context = ModifyItemWearablePower.createContext(player, stack);

			return ModifyItemWearablePower.modify(context, instances, this.slot(), () -> original.call(equippable, entityType));

		}

	}

	@Mixin(LivingEntity.class)
	public static abstract class Dispensing extends Entity {

		private Dispensing(EntityType<?> entityType, Level level) {
			super(entityType, level);
		}

		@WrapOperation(method = "canEquipWithDispenser", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/equipment/Equippable;canBeEquippedBy(Lnet/minecraft/world/entity/EntityType;)Z"))
		private boolean whenEquippable(Equippable equippable, EntityType<?> entityType, Operation<Boolean> original, ItemStack stack) {

			List<ModifyItemWearablePower.Instance> instances = PowersComponent.getInstances(this, ModifyItemWearablePower.Instance.class);
			Context context = ModifyItemWearablePower.createContext(this, stack);

			return ModifyItemWearablePower.modify(context, instances, equippable.slot(), () -> original.call(equippable, entityType));

		}

	}

}

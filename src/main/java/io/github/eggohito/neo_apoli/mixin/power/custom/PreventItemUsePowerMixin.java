package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.PreventItemUsePower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public abstract class PreventItemUsePowerMixin {

	@Mixin(ItemStack.class)
	public abstract static class StackUse {

		@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult preventUsage(Item item, World world, PlayerEntity user, Hand hand, Operation<ActionResult> original) {

			ItemStack thisAsStack = (ItemStack) (Object) this;
			Context context = PreventItemUsePower.createContext(world, user, hand, thisAsStack);

			if (PowersComponent.hasPowerImpl(user, PreventItemUsePower.Impl.class, impl -> impl.doesApply(context))) {
				return ActionResult.FAIL;
			}

			else {
				return original.call(item, world, user, hand);
			}

		}

	}

	@Mixin(BlockItem.class)
	public abstract static class BlockItemUse {

		@WrapOperation(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult preventUsageIfConsumable(BlockItem blockItem, World world, PlayerEntity user, Hand hand, Operation<ActionResult> original, ItemUsageContext usageContext) {

			ItemStack stack = usageContext.getStack();
			Context context = PreventItemUsePower.createContext(world, user, hand, stack);

			if (PowersComponent.hasPowerImpl(user, PreventItemUsePower.Impl.class, impl -> impl.doesApply(context))) {
				return ActionResult.FAIL;
			}

			else {
				return original.call(blockItem, world, user, hand);
			}

		}

	}

}

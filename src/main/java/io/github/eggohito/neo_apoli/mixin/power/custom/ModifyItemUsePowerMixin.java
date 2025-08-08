package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.ModifyItemUsePower;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class ModifyItemUsePowerMixin {

	@Mixin(ItemStack.class)
	public static abstract class StackUse {

		@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult beforeOnUse(Item item, World world, PlayerEntity user, Hand hand, Operation<ActionResult> original, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<ActionResult> zeroPriorityResultRef) {

			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = instant
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(item, world, user, hand));

		}

		@ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult afterOnUse(ActionResult original, World world, PlayerEntity user, Hand hand, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<ActionResult> zeroPriorityResultRef) {

			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = user.getStackInHand(hand).getMaxUseTime(user) == 0
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

		}

		@WrapOperation(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;"))
		private ItemStack beforeFinishUsing(Item item, ItemStack stack, World world, LivingEntity user, Operation<ItemStack> original) {

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), newStack -> user.setStackInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.BEFORE, result -> {}, () -> ActionResult.PASS, () -> ActionResult.PASS);
			ItemStack referredStack = stackReference.get();

			return original.call(referredStack.getItem(), referredStack, world, user);

		}

		@ModifyExpressionValue(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;"))
		private ItemStack afterFinishUsing(ItemStack original, World world, LivingEntity user) {

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.AFTER, result -> {}, () -> ActionResult.PASS, () -> ActionResult.PASS);
			return stackReference.get();

		}

		@WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Z"))
		private boolean beforeStoppedUsing(Item item, ItemStack stack, World world, LivingEntity user, int remainingUseTicks, Operation<Boolean> original, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), newStack -> user.setStackInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.BEFORE, result -> {}, () -> ActionResult.PASS, () -> ActionResult.PASS);
			ItemStack referredStack = stackReference.get();

			boolean result = original.call(referredStack.getItem(), referredStack, world, user, remainingUseTicks);
			stoppedUsingResultRef.set(result);

			return result;

		}

		@Inject(method = "onStoppedUsing", at = @At("TAIL"))
		private void afterStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

			ActionResult result = stoppedUsingResultRef.get()
				? ActionResult.SUCCESS
				: ActionResult.PASS;

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> result);

		}

		@WrapOperation(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;I)V"))
		private void beforeDuringTick(Item item, World world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), newStack -> user.setStackInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.BEFORE, newResult -> {}, () -> null, () -> ActionResult.PASS);
			ItemStack referredStack = stackReference.get();

			original.call(referredStack.getItem(), world, user, referredStack, remainingUseTicks);

		}

		@Inject(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;I)V", shift = At.Shift.AFTER))
		private void afterDuringTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {

			Hand hand = user.getActiveHand();
			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> ActionResult.PASS);

		}

	}

	@Mixin(BlockItem.class)
	public static abstract class BlockItemUse {

		@WrapOperation(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult beforeOnConsumableUse(BlockItem blockItem, World world, PlayerEntity user, Hand hand, Operation<ActionResult> original, ItemUsageContext usageContext, @Share("zeroPriorityResult") LocalRef<ActionResult> zeroPriorityResultRef) {

			StackReference stackReference = StackReference.of(() -> user.getStackInHand(hand), stack -> user.setStackInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = usageContext.getStack().getMaxUseTime(user) == 0
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(blockItem, world, user, hand));

		}

		@ModifyExpressionValue(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
		private ActionResult afterOnConsumableUse(ActionResult original, ItemUsageContext usageContext, @Share("zeroPriorityResult") LocalRef<ActionResult> zeroPriorityResultRef) {

			PlayerEntity player = usageContext.getPlayer();
			Hand hand = usageContext.getHand();

			if (player != null) {

				StackReference stackReference = StackReference.of(() -> player.getStackInHand(hand), stack -> player.setStackInHand(hand, stack));
				ModifyItemUsePower.TriggerType triggerType = player.getStackInHand(hand).getMaxUseTime(player) == 0
					? ModifyItemUsePower.TriggerType.INSTANT
					: ModifyItemUsePower.TriggerType.START;

				return ModifyItemUsePower.execute(usageContext.getWorld(), player, hand, stackReference, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

			}

			else {
				return original;
			}

		}

	}

}

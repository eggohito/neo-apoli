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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class ModifyItemUsePowerMixin {

	@Mixin(ItemStack.class)
	public static abstract class StackUse {

		@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
		private InteractionResult beforeOnUse(Item item, Level world, Player user, InteractionHand hand, Operation<InteractionResult> original, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = instant
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(item, world, user, hand));

		}

		@ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
		private InteractionResult afterOnUse(InteractionResult original, Level world, Player user, InteractionHand hand, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = user.getItemInHand(hand).getUseDuration(user) == 0
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

		}

		@WrapOperation(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
		private ItemStack beforeFinishUsing(Item item, ItemStack stack, Level world, LivingEntity user, Operation<ItemStack> original) {

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.BEFORE, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
			ItemStack referredStack = stackReference.get();

			return original.call(referredStack.getItem(), referredStack, world, user);

		}

		@ModifyExpressionValue(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
		private ItemStack afterFinishUsing(ItemStack original, Level world, LivingEntity user) {

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.AFTER, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
			return stackReference.get();

		}

		@WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
		private boolean beforeStoppedUsing(Item item, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, Operation<Boolean> original, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.BEFORE, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
			ItemStack referredStack = stackReference.get();

			boolean result = original.call(referredStack.getItem(), referredStack, world, user, remainingUseTicks);
			stoppedUsingResultRef.set(result);

			return result;

		}

		@Inject(method = "releaseUsing", at = @At("TAIL"))
		private void afterStoppedUsing(Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

			InteractionResult result = stoppedUsingResultRef.get()
				? InteractionResult.SUCCESS
				: InteractionResult.PASS;

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> result);

		}

		@WrapOperation(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"))
		private void beforeDuringTick(Item item, Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.BEFORE, newResult -> {}, () -> null, () -> InteractionResult.PASS);
			ItemStack referredStack = stackReference.get();

			original.call(referredStack.getItem(), world, user, referredStack, remainingUseTicks);

		}

		@Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V", shift = At.Shift.AFTER))
		private void afterDuringTick(Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {

			InteractionHand hand = user.getUsedItemHand();
			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

			ModifyItemUsePower.execute(world, user, hand, stackReference, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> InteractionResult.PASS);

		}

	}

	@Mixin(BlockItem.class)
	public static abstract class BlockItemUse {

		@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
		private InteractionResult beforeOnConsumableUse(BlockItem blockItem, Level world, Player user, InteractionHand hand, Operation<InteractionResult> original, UseOnContext usageContext, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

			SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = usageContext.getItemInHand().getUseDuration(user) == 0
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(blockItem, world, user, hand));

		}

		@ModifyExpressionValue(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
		private InteractionResult afterOnConsumableUse(InteractionResult original, UseOnContext usageContext, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

			Player player = usageContext.getPlayer();
			InteractionHand hand = usageContext.getHand();

			if (player != null) {

				SlotAccess stackReference = SlotAccess.of(() -> player.getItemInHand(hand), stack -> player.setItemInHand(hand, stack));
				ModifyItemUsePower.TriggerType triggerType = player.getItemInHand(hand).getUseDuration(player) == 0
					? ModifyItemUsePower.TriggerType.INSTANT
					: ModifyItemUsePower.TriggerType.START;

				return ModifyItemUsePower.execute(usageContext.getLevel(), player, hand, stackReference, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

			}

			else {
				return original;
			}

		}

	}

}

package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_item_use;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult beforeOnUse(Item item, Level world, Player user, InteractionHand hand, Operation<InteractionResult> original, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

		SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
		ModifyItemUsePower.TriggerType triggerType = instant
			? ModifyItemUsePower.TriggerType.INSTANT
			: ModifyItemUsePower.TriggerType.START;

		return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(item, world, user, hand));

	}

	@ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult afterOnUse(InteractionResult original, Level world, Player user, InteractionHand hand, @Local boolean instant, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

		SlotAccess stackReference = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
		ModifyItemUsePower.TriggerType triggerType = user.getItemInHand(hand).getUseDuration(user) == 0
			? ModifyItemUsePower.TriggerType.INSTANT
			: ModifyItemUsePower.TriggerType.START;

		return ModifyItemUsePower.execute(world, user, hand, stackReference, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

	}

	@WrapOperation(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
	ItemStack beforeFinishUsing(Item item, ItemStack stack, Level world, LivingEntity user, Operation<ItemStack> original) {

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.BEFORE, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
		ItemStack accessedStack = slotAccess.get();

		return original.call(accessedStack.getItem(), accessedStack, world, user);

	}

	@ModifyExpressionValue(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
	ItemStack afterFinishUsing(ItemStack original, Level world, LivingEntity user) {

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.FINISH, PriorityPhase.AFTER, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
		return slotAccess.get();

	}

	@WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
	boolean beforeStoppedUsing(Item item, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, Operation<Boolean> original, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.BEFORE, result -> {}, () -> InteractionResult.PASS, () -> InteractionResult.PASS);
		ItemStack accessedStack = slotAccess.get();

		boolean result = original.call(accessedStack.getItem(), accessedStack, world, user, remainingUseTicks);
		stoppedUsingResultRef.set(result);

		return result;

	}

	@Inject(method = "releaseUsing", at = @At("TAIL"))
	void afterStoppedUsing(Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, @Share("stoppedUsingResult") LocalBooleanRef stoppedUsingResultRef) {

		InteractionResult result = stoppedUsingResultRef.get()
			? InteractionResult.SUCCESS
			: InteractionResult.PASS;

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.STOP, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> result);

	}

	@WrapOperation(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"))
	void beforeDuringTick(Item item, Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), newStack -> user.setItemInHand(hand, newStack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.BEFORE, newResult -> {}, () -> null, () -> InteractionResult.PASS);
		ItemStack accessedStack = slotAccess.get();

		original.call(accessedStack.getItem(), world, user, accessedStack, remainingUseTicks);

	}

	@Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V", shift = At.Shift.AFTER))
	void afterDuringTick(Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {

		InteractionHand hand = user.getUsedItemHand();
		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));

		ModifyItemUsePower.execute(world, user, hand, slotAccess, ModifyItemUsePower.TriggerType.DURING, PriorityPhase.AFTER, newResult -> {}, () -> null, () -> InteractionResult.PASS);

	}

}

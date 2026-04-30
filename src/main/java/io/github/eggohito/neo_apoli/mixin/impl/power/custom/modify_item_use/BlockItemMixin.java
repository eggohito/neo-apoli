package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_item_use;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.ModifyItemUsePower;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult beforeOnConsumableUse(BlockItem blockItem, Level world, Player user, InteractionHand hand, Operation<InteractionResult> original, UseOnContext usageContext, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

		SlotAccess slotAccess = SlotAccess.of(() -> user.getItemInHand(hand), stack -> user.setItemInHand(hand, stack));
		ModifyItemUsePower.TriggerType triggerType = usageContext.getItemInHand().getUseDuration(user) == 0
			? ModifyItemUsePower.TriggerType.INSTANT
			: ModifyItemUsePower.TriggerType.START;

		return ModifyItemUsePower.execute(world, user, hand, slotAccess, triggerType, PriorityPhase.BEFORE, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original.call(blockItem, world, user, hand));

	}

	@ModifyExpressionValue(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult afterOnConsumableUse(InteractionResult original, UseOnContext usageContext, @Share("zeroPriorityResult") LocalRef<InteractionResult> zeroPriorityResultRef) {

		Player player = usageContext.getPlayer();
		InteractionHand hand = usageContext.getHand();

		if (player != null) {

			SlotAccess slotAccess = SlotAccess.of(() -> player.getItemInHand(hand), stack -> player.setItemInHand(hand, stack));
			ModifyItemUsePower.TriggerType triggerType = player.getItemInHand(hand).getUseDuration(player) == 0
				? ModifyItemUsePower.TriggerType.INSTANT
				: ModifyItemUsePower.TriggerType.START;

			return ModifyItemUsePower.execute(usageContext.getLevel(), player, hand, slotAccess, triggerType, PriorityPhase.AFTER, zeroPriorityResultRef::set, zeroPriorityResultRef::get, () -> original);

		}

		else {
			return original;
		}

	}

}

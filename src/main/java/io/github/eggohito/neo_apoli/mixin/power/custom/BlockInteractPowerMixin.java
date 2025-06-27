package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.BlockInteractPower;
import io.github.eggohito.neo_apoli.util.BlockInteractionPhase;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class BlockInteractPowerMixin {

	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
	private ActionResult beforeUseBlock(BlockState blockState, World world, PlayerEntity player, BlockHitResult blockHitResult, Operation<ActionResult> original, ServerPlayerEntity mPlayer, World mWorld, ItemStack mStack, Hand mHand, @Share("zeroPriority$onBlockResult") LocalRef<ActionResult> zeroPriority$onBlockResultRef) {
		return BlockInteractPower.execute(player, mHand, blockHitResult, BlockInteractionPhase.BLOCK, PriorityPhase.BEFORE, zeroPriority$onBlockResultRef::set, zeroPriority$onBlockResultRef::get, () -> original.call(blockState, world, player, blockHitResult));
	}

	@ModifyVariable(method = "interactBlock", at = @At("STORE"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")), ordinal = 0)
	private ActionResult afterUseBlock(ActionResult original, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, @Share("zeroPriority$blockUseResult") LocalRef<ActionResult> zeroPriority$blockUseResultRef) {
		return BlockInteractPower.execute(player, hand, blockHitResult, BlockInteractionPhase.BLOCK, PriorityPhase.AFTER, zeroPriority$blockUseResultRef::set, zeroPriority$blockUseResultRef::get, () -> original);
	}

	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
	private ActionResult beforeUseBlockWithItem(BlockState blockState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult blockHitResult, Operation<ActionResult> original, @Share("zeroPriority$withItemResult") LocalRef<ActionResult> zeroPriority$withItemResultRef) {
		return BlockInteractPower.execute(player, hand, blockHitResult, BlockInteractionPhase.BLOCK_WITH_ITEM, PriorityPhase.BEFORE, zeroPriority$withItemResultRef::set, zeroPriority$withItemResultRef::get, () -> original.call(blockState, player.getStackInHand(hand), world, player, hand, blockHitResult));
	}

	@ModifyVariable(method = "interactBlock", at = @At("STORE"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")), ordinal = 0)
	private ActionResult afterUseBlockWithItem(ActionResult original, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, @Share("zeroPriority$withItemResult") LocalRef<ActionResult> zeroPriority$withItemResultRef) {
		return BlockInteractPower.execute(player, hand, blockHitResult, BlockInteractionPhase.BLOCK_WITH_ITEM, PriorityPhase.AFTER, zeroPriority$withItemResultRef::set, zeroPriority$withItemResultRef::get, () -> original);
	}

	//	TODO: Decide whether to integrate "using an item on block" to the `block_interact` power type, or implement it
	//		  as a different power type
//	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
//	private ActionResult beforeUseItemOnBlock(ItemStack stack, ItemUsageContext context, Operation<ActionResult> original, ServerPlayerEntity mPlayer, World mWorld, ItemStack mStack, Hand mHand, BlockHitResult mBlockHitResult, @Share("zeroPriority$itemOnBlockResult") LocalRef<ActionResult> zeroPriority$itemOnBlockResultRef) {
//		return BlockInteractPower.execute(context.getPlayer(), context.getHand(), mBlockHitResult, BlockInteractionPhase.ITEM_ON_BLOCK, PriorityPhase.BEFORE, zeroPriority$itemOnBlockResultRef::set, zeroPriority$itemOnBlockResultRef::get, () -> original.call(stack, context));
//	}
//
//	@ModifyVariable(method = "interactBlock", at = @At("STORE"), slice = @Slice(from = @At(value = "NEW", target = "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/item/ItemUsageContext;")))
//	private ActionResult afterUseItemOnBlock(ActionResult original, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, @Share("zeroPriority$itemOnBlockResult") LocalRef<ActionResult> zeroPriority$itemOnBlockResultRef) {
//		return BlockInteractPower.execute(player, hand, blockHitResult, BlockInteractionPhase.ITEM_ON_BLOCK, PriorityPhase.AFTER, zeroPriority$itemOnBlockResultRef::set, zeroPriority$itemOnBlockResultRef::get, () -> original);
//	}


}

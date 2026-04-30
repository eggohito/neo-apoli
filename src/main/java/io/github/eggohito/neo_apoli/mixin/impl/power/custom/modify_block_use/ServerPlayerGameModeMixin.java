package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_block_use;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockUsePower;
import io.github.eggohito.neo_apoli.util.BlockUsePhase;
import io.github.eggohito.neo_apoli.util.PriorityPhase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useWithoutItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult beforeUseBlock(BlockState blockState, Level level, Player player, BlockHitResult blockHitResult, Operation<InteractionResult> original, ServerPlayer mServerPlayer, Level mLevel, ItemStack mStack, InteractionHand mHand, @Share("zeroPriority$onBlockResult") LocalRef<InteractionResult> zeroPriority$onBlockResultRef) {
		return ModifyBlockUsePower.execute(player, mHand, blockHitResult, BlockUsePhase.BLOCK, PriorityPhase.BEFORE, zeroPriority$onBlockResultRef::set, zeroPriority$onBlockResultRef::get, () -> original.call(blockState, level, player, blockHitResult));
	}

	@ModifyVariable(method = "useItemOn", at = @At("STORE"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useWithoutItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")), ordinal = 0)
	InteractionResult afterUseBlock(InteractionResult original, ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult blockHitResult, @Share("zeroPriority$blockUseResult") LocalRef<InteractionResult> zeroPriority$blockUseResultRef) {
		return ModifyBlockUsePower.execute(player, hand, blockHitResult, BlockUsePhase.BLOCK, PriorityPhase.AFTER, zeroPriority$blockUseResultRef::set, zeroPriority$blockUseResultRef::get, () -> original);
	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	InteractionResult beforeUseBlockWithItem(BlockState blockState, ItemStack stack, Level level, Player player, InteractionHand hand, BlockHitResult blockHitResult, Operation<InteractionResult> original, @Share("zeroPriority$withItemResult") LocalRef<InteractionResult> zeroPriority$withItemResultRef) {
		return ModifyBlockUsePower.execute(player, hand, blockHitResult, BlockUsePhase.BLOCK_WITH_ITEM, PriorityPhase.BEFORE, zeroPriority$withItemResultRef::set, zeroPriority$withItemResultRef::get, () -> original.call(blockState, stack, level, player, hand, blockHitResult));
	}

	@ModifyVariable(method = "useItemOn", at = @At("STORE"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")), ordinal = 0)
	InteractionResult afterUseBlockWithItem(InteractionResult original, ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult blockHitResult, @Share("zeroPriority$withItemResult") LocalRef<InteractionResult> zeroPriority$withItemResultRef) {
		return ModifyBlockUsePower.execute(player, hand, blockHitResult, BlockUsePhase.BLOCK_WITH_ITEM, PriorityPhase.AFTER, zeroPriority$withItemResultRef::set, zeroPriority$withItemResultRef::get, () -> original);
	}

	//	TODO: Decide whether to integrate "using an item on block" to the `block_interact` power type, or implement it
	//		  as a different power type
//	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
//	ActionResult beforeUseItemOnBlock(ItemStack stack, ItemUsageContext context, Operation<ActionResult> original, ServerPlayerEntity mPlayer, World mWorld, ItemStack mStack, Hand mHand, BlockHitResult mBlockHitResult, @Share("zeroPriority$itemOnBlockResult") LocalRef<ActionResult> zeroPriority$itemOnBlockResultRef) {
//		return ModifyBlockUsePower.execute(context.getPlayer(), context.getHand(), mBlockHitResult, BlockUsePhase.ITEM_ON_BLOCK, PriorityPhase.BEFORE, zeroPriority$itemOnBlockResultRef::set, zeroPriority$itemOnBlockResultRef::get, () -> original.call(stack, context));
//	}
//
//	@ModifyVariable(method = "interactBlock", at = @At("STORE"), slice = @Slice(from = @At(value = "NEW", target = "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/item/ItemUsageContext;")))
//	ActionResult afterUseItemOnBlock(ActionResult original, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, @Share("zeroPriority$itemOnBlockResult") LocalRef<ActionResult> zeroPriority$itemOnBlockResultRef) {
//		return ModifyBlockUsePower.execute(player, hand, blockHitResult, BlockUsePhase.ITEM_ON_BLOCK, PriorityPhase.AFTER, zeroPriority$itemOnBlockResultRef::set, zeroPriority$itemOnBlockResultRef::get, () -> original);
//	}


}

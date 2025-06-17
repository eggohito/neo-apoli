package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.BlockBreakPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class BlockBreakPowerMixin {

	@Shadow
	@Final
	protected ServerPlayerEntity player;

	@Shadow
	protected ServerWorld world;

	@Unique
	protected WeakReference<Direction> neo_apoli$blockBreakDirection;

	@Unique
	protected boolean neo_apoli$harvested;

	@Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
	private void cacheDirection(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
		this.neo_apoli$blockBreakDirection = new WeakReference<>(direction);
		this.neo_apoli$harvested = false;
	}

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"))
	private void cacheBlockStateAndEntity(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockState state, @Local BlockEntity blockEntity, @Share("brokenBlockState") LocalRef<BlockState> brokenBlockStateRef, @Share("brokenBlockEntity") LocalRef<BlockEntity> brokenBlockEntityRef) {
		brokenBlockStateRef.set(state);
		brokenBlockEntityRef.set(blockEntity);
	}

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"))
	private void cacheHarvestedStatus(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean blockRemoved, @Local(ordinal = 1) boolean canHarvest) {
		this.neo_apoli$harvested = blockRemoved && canHarvest;
	}

	@Inject(method = "tryBreakBlock", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")))
	private void onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Share("brokenBlockState") LocalRef<BlockState> brokenBlockStateRef, @Share("brokenBlockEntity") LocalRef<BlockEntity> brokenBlockEntityRef) {

		BlockState brokenBlockState = brokenBlockStateRef.get();

		if (brokenBlockState != null) {
			BlockBreakPower.execute(this.player, pos, brokenBlockState, brokenBlockEntityRef.get(), this.neo_apoli$blockBreakDirection.get(), this.neo_apoli$harvested);
		}

	}

}

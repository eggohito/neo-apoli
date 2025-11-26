package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.power.custom.CallbackBlockBreakPower;
import io.github.eggohito.neo_apoli.util.context.Context;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ServerPlayerGameMode.class)
public abstract class CallbackBlockBreakPowerMixin {

	@Shadow
	@Final
	protected ServerPlayer player;

	@Shadow
	protected ServerLevel level;

	@Unique
	protected WeakReference<Direction> neo_apoli$blockBreakDirection;

	@Unique
	protected boolean neo_apoli$harvested;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	private void cacheDirection(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
		this.neo_apoli$blockBreakDirection = new WeakReference<>(direction);
		this.neo_apoli$harvested = false;
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private void cacheBlockStateAndEntity(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local BlockState state, @Local BlockEntity blockEntity, @Share("brokenBlockState") LocalRef<BlockState> brokenBlockStateRef, @Share("brokenBlockEntity") LocalRef<BlockEntity> brokenBlockEntityRef) {
		brokenBlockStateRef.set(state);
		brokenBlockEntityRef.set(blockEntity);
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
	private void cacheHarvestedStatus(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean blockRemoved, @Local(ordinal = 1) boolean canHarvest) {
		this.neo_apoli$harvested = blockRemoved && canHarvest;
	}

	@Inject(method = "destroyBlock", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;")))
	private void onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Share("brokenBlockState") LocalRef<BlockState> brokenBlockStateRef, @Share("brokenBlockEntity") LocalRef<BlockEntity> brokenBlockEntityRef) {

		Context context = CallbackBlockBreakPower.createContext(this.player, pos, brokenBlockStateRef.get(), brokenBlockEntityRef.get(), this.neo_apoli$blockBreakDirection.get());

		CallbackBlockBreakPower.execute(context, this.neo_apoli$harvested);

	}

}

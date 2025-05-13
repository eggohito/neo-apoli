package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.duck.BlockBreakingContextAccess;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;

@Mixin(value = ServerPlayerInteractionManager.class, priority = 800)
public abstract class ServerPlayerInteractionMixin implements BlockBreakingContextAccess {

	@Shadow
	protected ServerWorld world;

	@Unique
	private WeakReference<SavedBlockPosition> brokenBlockCache;

	@Unique
	private WeakReference<Direction> brokenBlockDirection;

	@Unique
	private boolean harvested;

	@Override
	public SavedBlockPosition neo_apoli$getBrokenBlockCache() {
		return brokenBlockCache.get();
	}

	@Override
	public Direction neo_apoli$getBrokenBlockDirection() {
		return brokenBlockDirection.get();
	}

	@Override
	public boolean neo_apoli$wasHarvested() {
		return harvested;
	}

	@Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
	private void cacheBlockAndDirection(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
		this.brokenBlockCache = new WeakReference<>(new SavedBlockPosition(this.world, pos, false));
		this.brokenBlockDirection = new WeakReference<>(direction);
		this.harvested = false;
	}

	@Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")))
	private void cacheHarvestedStatus(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean blockRemoved, @Local(ordinal = 1) boolean canHarvest) {
		this.harvested = blockRemoved && canHarvest;
	}

}

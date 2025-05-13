package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.duck.BlockBreakingContextAccess;
import io.github.eggohito.neo_apoli.power.custom.OnBlockBreakPower;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class BlockBreakPowerMixin implements BlockBreakingContextAccess {

	@Shadow
	@Final
	protected ServerPlayerEntity player;

	@Shadow
	protected ServerWorld world;

	@Inject(method = "tryBreakBlock", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")))
	private void onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

		SavedBlockPosition brokenBlockCache = this.neo_apoli$getBrokenBlockCache();
		Direction direction = this.neo_apoli$getBrokenBlockDirection();

		if (brokenBlockCache != null) {
			OnBlockBreakPower.execute(this.player, this.world, pos, brokenBlockCache.getBlockState(), brokenBlockCache.getBlockEntity(), direction, this.neo_apoli$wasHarvested());
		}

	}

}

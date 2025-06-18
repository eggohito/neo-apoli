package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.BlockHarvestPower;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

public abstract class BlockHarvestPowerMixin {

	@Mixin(AbstractBlock.class)
	public abstract static class BlockBreakingDeltaProxy {

		@WrapOperation(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z"))
		private boolean modifyHarvestability(PlayerEntity player, BlockState state, Operation<Boolean> original, BlockState mState, PlayerEntity mPlayer, BlockView mWorld, BlockPos mPos) {
			return BlockHarvestPower.canHarvest(player, mPos, state, mWorld.getBlockEntity(mPos), () -> original.call(player, state));
		}

	}

	@Mixin(ServerPlayerInteractionManager.class)
	public abstract static class HarvestabilityProxy {

		@Shadow
		protected ServerWorld world;

		@WrapOperation(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z"))
		private boolean modifyHarvestability(ServerPlayerEntity serverPlayer, BlockState state, Operation<Boolean> original, BlockPos mPos, @Local BlockEntity blockEntity) {
			return BlockHarvestPower.canHarvest(serverPlayer, mPos, state, blockEntity, () -> original.call(serverPlayer, state));
		}

	}

}

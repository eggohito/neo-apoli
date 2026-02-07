package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

public abstract class ModifyBlockHarvestablePowerMixin {

	@Mixin(BlockBehaviour.class)
	public abstract static class BlockBreakingDeltaProxy {

		@WrapOperation(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
		private boolean neo_apoli$modifyHarvestable(Player player, BlockState blockState, Operation<Boolean> original, BlockState mBlockState, Player mPlayer, BlockGetter mBlockGetter, BlockPos mBlockPos) {

			try {
				return ModifyBlockHarvestablePower.modify(player, mBlockPos, blockState, mBlockGetter.getBlockEntity(mBlockPos), () -> original.call(player, blockState));
			}

			finally {
				ModifyBlockHarvestablePower.VISITOR.clear();
			}

		}

	}

	@Mixin(ServerPlayerGameMode.class)
	public abstract static class HarvestableProxy {

		@Shadow
		protected ServerLevel level;

		@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
		private boolean neo_apoli$modifyHarvestable(ServerPlayer serverPlayer, BlockState blockState, Operation<Boolean> original, BlockPos mBlockPos, @Local BlockEntity blockEntity) {

			try {
				return ModifyBlockHarvestablePower.modify(serverPlayer, mBlockPos, blockState, blockEntity, () -> original.call(serverPlayer, blockState));
			}

			finally {
				ModifyBlockHarvestablePower.VISITOR.clear();
			}

		}

	}

}

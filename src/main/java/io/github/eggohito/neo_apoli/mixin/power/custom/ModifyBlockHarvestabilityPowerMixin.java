package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestabilityPower;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyBlockHarvestabilityPowerMixin {

	@Mixin(AbstractBlock.class)
	public abstract static class BlockBreakingDeltaProxy {

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$blockHarvestContext = new ThreadLocal<>();

		@Unique
		protected Context neo_apoli$getOrCreateBlockHarvestContext(PlayerEntity player, BlockView blockView, BlockPos blockPos, BlockState blockState) {

			Context context = Optional.ofNullable(this.neo_apoli$blockHarvestContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> createContext(player, blockPos, blockState, blockView.getBlockEntity(blockPos)));

			this.neo_apoli$blockHarvestContext.set(new WeakReference<>(context));
			return context;

		}

		@WrapOperation(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z"))
		private boolean modifyHarvestability(PlayerEntity player, BlockState blockState, Operation<Boolean> original, BlockState mBlockState, PlayerEntity mPlayer, BlockView mBlockView, BlockPos mBlockPos) {

			Context context = this.neo_apoli$getOrCreateBlockHarvestContext(player, mBlockView, mBlockPos, blockState);
			boolean canHarvest = ModifyBlockHarvestabilityPower.canHarvest(context, () -> original.call(player, blockState));

			this.neo_apoli$blockHarvestContext.remove();
			return canHarvest;

		}

	}

	@Mixin(ServerPlayerInteractionManager.class)
	public abstract static class HarvestabilityProxy {

		@Unique
		protected WeakReference<Context> neo_apoli$blockHarvestContext = new WeakReference<>(null);

		@Unique
		protected Context neo_apoli$getOrCreateBlockHarvestContext(ServerPlayerEntity serverPlayer, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {

			Context context = Optional.ofNullable(this.neo_apoli$blockHarvestContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> createContext(serverPlayer, blockPos, blockState, blockEntity));

			this.neo_apoli$blockHarvestContext = new WeakReference<>(context);
			return context;

		}

		@Shadow
		protected ServerWorld world;

		@WrapOperation(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z"))
		private boolean modifyHarvestability(ServerPlayerEntity serverPlayer, BlockState blockState, Operation<Boolean> original, BlockPos mBlockPos, @Local BlockEntity blockEntity) {

			Context context = this.neo_apoli$getOrCreateBlockHarvestContext(serverPlayer, mBlockPos, blockState, blockEntity);
			boolean canHarvest = ModifyBlockHarvestabilityPower.canHarvest(context, () -> original.call(serverPlayer, blockState));

			this.neo_apoli$blockHarvestContext.clear();
			return canHarvest;

		}

	}

	protected static Context createContext(PlayerEntity player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_HARVESTABILITY.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, blockState)
			.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
			.add(ContextParameters.ENTITY, player)
			.add(ContextParameters.ENTITY_POS, player.getPos())
			.build(player.getWorld());
	}

}

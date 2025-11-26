package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyBlockHarvestablePower;
import io.github.eggohito.neo_apoli.util.context.Context;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ModifyBlockHarvestablePowerMixin {

	@Mixin(BlockBehaviour.class)
	public abstract static class BlockBreakingDeltaProxy {

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$blockHarvestContext = new ThreadLocal<>();

		@Unique
		protected Context neo_apoli$getOrCreateBlockHarvestContext(Player player, BlockGetter blockView, BlockPos blockPos, BlockState blockState) {

			Context context = Optional.ofNullable(this.neo_apoli$blockHarvestContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyBlockHarvestablePower.createContext(player, blockPos, blockState, blockView.getBlockEntity(blockPos)));

			this.neo_apoli$blockHarvestContext.set(new WeakReference<>(context));
			return context;

		}

		@WrapOperation(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
		private boolean neo_apoli$modifyHarvestable(Player player, BlockState blockState, Operation<Boolean> original, BlockState mBlockState, Player mPlayer, BlockGetter mBlockView, BlockPos mBlockPos) {

			Context context = this.neo_apoli$getOrCreateBlockHarvestContext(player, mBlockView, mBlockPos, blockState);
			boolean harvestable = ModifyBlockHarvestablePower.modify(context, () -> original.call(player, blockState));

			this.neo_apoli$blockHarvestContext.remove();
			return harvestable;

		}

	}

	@Mixin(ServerPlayerGameMode.class)
	public abstract static class HarvestableProxy {

		@Unique
		protected WeakReference<Context> neo_apoli$blockHarvestContext = new WeakReference<>(null);

		@Unique
		protected Context neo_apoli$getOrCreateBlockHarvestContext(ServerPlayer serverPlayer, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {

			Context context = Optional.ofNullable(this.neo_apoli$blockHarvestContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyBlockHarvestablePower.createContext(serverPlayer, blockPos, blockState, blockEntity));

			this.neo_apoli$blockHarvestContext = new WeakReference<>(context);
			return context;

		}

		@Shadow
		protected ServerLevel level;

		@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
		private boolean neo_apoli$modifyHarvestable(ServerPlayer serverPlayer, BlockState blockState, Operation<Boolean> original, BlockPos mBlockPos, @Local BlockEntity blockEntity) {

			Context context = this.neo_apoli$getOrCreateBlockHarvestContext(serverPlayer, mBlockPos, blockState, blockEntity);
			boolean harvestable = ModifyBlockHarvestablePower.modify(context, () -> original.call(serverPlayer, blockState));

			this.neo_apoli$blockHarvestContext.clear();
			return harvestable;

		}

	}

}

package io.github.eggohito.neo_apoli.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class SavedBlockPosition extends CachedBlockPosition {

	private final BlockState blockState;
	private final BlockEntity blockEntity;

	public SavedBlockPosition(WorldView worldView, BlockPos blockPos, BiFunction<WorldView, BlockPos, BlockState> blockStateGetter, BiFunction<WorldView, BlockPos, BlockEntity> blockEntityGetter) {
		super(worldView, blockPos, false);
		this.blockState = blockStateGetter.apply(worldView, blockPos);
		this.blockEntity = blockEntityGetter.apply(worldView, blockPos);
	}

	public SavedBlockPosition(WorldView worldView, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
		this(worldView, blockPos, (world, pos) -> blockState, (world, pos) -> blockEntity);
	}

	public SavedBlockPosition(WorldView worldView, BlockPos blockPos, boolean forceload) {
		this(worldView, blockPos, (world, pos) -> forceload || world.isChunkLoaded(pos) ? world.getBlockState(pos) : null, (world, pos) -> forceload || world.isChunkLoaded(pos) ? world.getBlockEntity(pos) : null);
	}

	@Override
	public BlockState getBlockState() {
		return blockState;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity() {
		return blockEntity;
	}

}

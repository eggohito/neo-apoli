package io.github.eggohito.neo_apoli.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class CachedBlock extends BlockInWorld {

	private final BlockState blockState;
	private final BlockEntity blockEntity;

	public CachedBlock(LevelReader worldView, BlockPos blockPos, BiFunction<LevelReader, BlockPos, BlockState> blockStateGetter, BiFunction<LevelReader, BlockPos, BlockEntity> blockEntityGetter) {
		super(worldView, blockPos, false);
		this.blockState = blockStateGetter.apply(worldView, blockPos);
		this.blockEntity = blockEntityGetter.apply(worldView, blockPos);
	}

	public CachedBlock(LevelReader worldView, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
		this(worldView, blockPos, (world, pos) -> blockState, (world, pos) -> blockEntity);
	}

	public CachedBlock(LevelReader worldView, BlockPos blockPos, boolean forceload) {
		this(worldView, blockPos, (world, pos) -> forceload || world.hasChunkAt(pos) ? world.getBlockState(pos) : null, (world, pos) -> forceload || world.hasChunkAt(pos) ? world.getBlockEntity(pos) : null);
	}

	@Override
	public BlockState getState() {
		return blockState;
	}

	@Nullable
	@Override
	public BlockEntity getEntity() {
		return blockEntity;
	}

}

package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.mixin.access.BlockInWorldAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record CachedBlock(BlockPos pos, BlockState state, @Nullable BlockEntity entity) {

	public static Optional<CachedBlock> optionallyFromLoadedPos(Level level, BlockPos pos) {

		try {
			return Optional.of(fromLoadedPos(level, pos));
		}

		catch (PosUnloadedException | PosOutOfBoundsException ignored) {
			return Optional.empty();
		}

	}

	@SuppressWarnings("ConstantValue")
	public static Optional<CachedBlock> optionallyFromWorld(BlockInWorld block) {
		return (((BlockInWorldAccessor) block).doesLoadChunks() || block.getState() != null)
			? Optional.of(new CachedBlock(block.getPos(), block.getState(), block.getEntity()))
			: Optional.empty();
	}

	public static CachedBlock fromLoadedPos(Level level, BlockPos pos) {

		if (!level.hasChunkAt(pos)) {
			throw new PosUnloadedException(level, pos);
		}

		else if (!level.isInWorldBounds(pos)) {
			throw new PosOutOfBoundsException(level, pos);
		}

		else {
			return fromPos(level, pos);
		}

	}

	public static CachedBlock fromPos(Level level, BlockPos pos) {
		return new CachedBlock(pos, level.getBlockState(pos), level.getBlockEntity(pos));
	}

}

package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record CachedBlock(BlockPos pos, BlockState state, @Nullable BlockEntity entity) {

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

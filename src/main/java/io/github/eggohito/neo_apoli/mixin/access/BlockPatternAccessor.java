package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockPattern.class)
public interface BlockPatternAccessor {

	@Invoker
	static BlockPos callTranslateAndRotate(BlockPos pos, Direction finger, Direction thumb, int palmOffset, int thumbOffset, int fingerOffset) {
		throw new AssertionError();
	}

}

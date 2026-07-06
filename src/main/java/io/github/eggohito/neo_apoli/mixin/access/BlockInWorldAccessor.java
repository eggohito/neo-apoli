package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockInWorld.class)
public interface BlockInWorldAccessor {

	@Accessor("loadChunks")
	boolean doesLoadChunks();

}

package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockInput.class)
public interface BlockInputAccessor {

	@Nullable
	@Accessor
	CompoundTag getTag();

}

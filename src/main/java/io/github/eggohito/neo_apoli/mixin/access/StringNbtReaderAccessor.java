package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StringNbtReader.class)
public interface StringNbtReaderAccessor {

	@Accessor("DEFAULT_READER")
	static StringNbtReader<NbtElement> getDefaultReader() {
		throw new AssertionError();
	}

}

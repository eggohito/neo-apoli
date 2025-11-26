package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagParser.class)
public interface TagParserAccessor {

	@Accessor("NBT_OPS_PARSER")
	static TagParser<Tag> getDefaultReader() {
		throw new AssertionError();
	}

}

package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryAccessor {

	@Accessor
	Identifier getId();

	@Accessor
	boolean isTag();

	@Accessor
	boolean isRequired();

}

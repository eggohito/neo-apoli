package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryAccessor {

	@Accessor
	ResourceLocation getId();

	@Accessor
	boolean isTag();

	@Accessor
	boolean isRequired();

}

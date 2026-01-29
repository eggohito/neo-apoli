package io.github.eggohito.neo_apoli.client.mixin.access;

import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AtlasSet.class)
public interface AtlasSetAccessor {

	@Accessor("atlases")
	Map<ResourceLocation, AtlasSet.AtlasEntry> getEntries();

}

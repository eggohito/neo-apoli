package io.github.eggohito.neo_apoli.mixin.impl.event.tags_built;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.event.TagsBuilt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {

	@Shadow
	@Final
	private String directory;

	@Inject(method = "build", at = @At("RETURN"))
	private <T> void hookOnBuild(Map<ResourceLocation, List<TagLoader.EntryWithSource>> builders, CallbackInfoReturnable<Map<ResourceLocation, List<T>>> cir, @Local TagEntry.Lookup<T> lookup, @Local DependencySorter<ResourceLocation, TagLoader.SortingEntry> sorter) {
		TagsBuilt.EVENT.invoker().onBuild(this.directory, lookup, sorter);
	}

}

package io.github.eggohito.neo_apoli.api.tag;

import io.github.eggohito.neo_apoli.impl.tag.NestedTagCacheImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Set;

public interface NestedTagCache<T> {

	ResourceKey<? extends Registry<T>> registry();

	Set<TagKey<T>> getOrEmpty(TagKey<T> tag);

	@SuppressWarnings("unchecked")
	static <T> NestedTagCache<T> getOrCreate(ResourceKey<? extends Registry<T>> registry) {
		return (NestedTagCache<T>) NestedTagCacheImpl.GLOBAL.computeIfAbsent(registry, k -> new NestedTagCacheImpl<>(registry));
	}

}

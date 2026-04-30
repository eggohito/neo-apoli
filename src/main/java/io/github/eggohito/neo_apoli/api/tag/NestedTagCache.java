package io.github.eggohito.neo_apoli.api.tag;

import io.github.eggohito.neo_apoli.impl.tag.NestedTagCacheImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Set;

/**
 *  An API used for querying tags nested in tags, which is useful if you ever need to check if a tag is included in
 *  a tag.
 * @see io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower
 */
public interface NestedTagCache<T> {

	ResourceKey<? extends Registry<T>> registry();

	Set<TagKey<T>> getOrEmpty(TagKey<T> tag);

	@SuppressWarnings("unchecked")
	static <T> NestedTagCache<T> getOrCreate(ResourceKey<? extends Registry<T>> registry) {
		return (NestedTagCache<T>) NestedTagCacheImpl.GLOBAL.computeIfAbsent(registry, k -> new NestedTagCacheImpl<>(registry));
	}

}

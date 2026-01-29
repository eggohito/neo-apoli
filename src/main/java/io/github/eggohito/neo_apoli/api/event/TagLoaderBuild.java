package io.github.eggohito.neo_apoli.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;

/**
 *  An event used for hooking into when tags from {@link TagLoader} are built. Useful for caching tags where querying tags
 *  from the registry does not fit one's use-case
 */
public interface TagLoaderBuild {

	Event<TagLoaderBuild> EVENT = EventFactory.createArrayBacked(
		TagLoaderBuild.class,
		callbacks -> new TagLoaderBuild() {

			@Override
			public <T> void onBuild(String directory, TagEntry.Lookup<T> lookup, DependencySorter<ResourceLocation, TagLoader.SortingEntry> sorter) {

				for (var callback : callbacks) {
					callback.onBuild(directory, lookup, sorter);
				}

			}

		}
	);

	<T> void onBuild(String directory, TagEntry.Lookup<T> lookup, DependencySorter<ResourceLocation, TagLoader.SortingEntry> sorter);

}

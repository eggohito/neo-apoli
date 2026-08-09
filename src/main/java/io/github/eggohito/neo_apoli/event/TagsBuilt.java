package io.github.eggohito.neo_apoli.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;

/**
 *  An event invoked when tags from a {@link TagLoader} instance are built.
 *  @see io.github.eggohito.neo_apoli.tag.NestedTag
 */
public interface TagsBuilt {

	Event<TagsBuilt> EVENT = EventFactory.createArrayBacked(
		TagsBuilt.class,
		callbacks -> new TagsBuilt() {

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

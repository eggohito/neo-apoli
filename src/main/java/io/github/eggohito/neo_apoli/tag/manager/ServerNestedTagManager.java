package io.github.eggohito.neo_apoli.tag.manager;

import com.google.common.collect.ImmutableSetMultimap;
import io.github.eggohito.neo_apoli.event.TagsBuilt;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdateNestedTagPacket;
import io.github.eggohito.neo_apoli.tag.NestedTag;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class ServerNestedTagManager implements NestedTagManager {

	protected final Map<ResourceKey<?>, NestedTag<?>> registry = new Object2ObjectOpenHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> NestedTag<T> getOrCreate(ResourceKey<? extends Registry<T>> registryKey) {
		return (NestedTag<T>) this.registry.computeIfAbsent(registryKey, k -> this.create(registryKey));
	}

	@Override
	public void init() {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> this.send(player));
	}

	private <T> NestedTag<T> create(ResourceKey<? extends Registry<T>> registryKey) {

		NestedTag<T> nestedTag = new NestedTag<>(registryKey, ImmutableSetMultimap.of());
		String targetDirectory = Registries.tagsDirPath(registryKey);

		TagsBuilt.EVENT.register(registryKey.location(), new TagsBuilt() {

			@Override
			public <I> void onBuild(String directory, TagEntry.Lookup<I> lookup, DependencySorter<ResourceLocation, TagLoader.SortingEntry> sorter) {

				if (!directory.equals(targetDirectory)) {
					return;
				}

				ImmutableSetMultimap.Builder<TagKey<T>, TagKey<T>> builder = ImmutableSetMultimap.builder();
				sorter.orderByDependencies((id, sorting) -> sorting.entries()
					.stream()
					.map(TagLoader.EntryWithSource::entry)
					.filter(entry -> entry.build(lookup, Consumers.nop()))
					.map(TagEntryAccessor.class::cast)
					.filter(TagEntryAccessor::isTag)
					.map(accessor -> TagKey.create(registryKey, accessor.getId()))
					.forEach(tag -> builder.put(tag, TagKey.create(registryKey, id))));

				ServerNestedTagManager.this.registry.put(registryKey, new NestedTag<>(registryKey, builder.build()));

			}

		});

		return nestedTag;

	}

	private void send(ServerPlayer recipient) {

		for (var nestedTag : this.registry.values()) {
			ServerPlayNetworking.send(recipient, new ClientboundUpdateNestedTagPacket<>(nestedTag));
		}

	}

}

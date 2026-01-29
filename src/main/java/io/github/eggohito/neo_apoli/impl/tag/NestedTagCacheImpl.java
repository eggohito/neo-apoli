package io.github.eggohito.neo_apoli.impl.tag;

import io.github.eggohito.neo_apoli.api.event.TagLoaderBuild;
import io.github.eggohito.neo_apoli.api.tag.NestedTagCache;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeTagCacheS2CPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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

import java.util.Map;
import java.util.Set;

public class NestedTagCacheImpl<T> implements NestedTagCache<T> {

	public static final Map<ResourceKey<? extends Registry<?>>, NestedTagCacheImpl<?>> GLOBAL = new Object2ObjectOpenHashMap<>();

	protected final ResourceKey<? extends Registry<T>> registry;
	protected final Map<TagKey<T>, Set<TagKey<T>>> cache;

	protected NestedTagCacheImpl(ResourceKey<? extends Registry<T>> registry, Map<TagKey<T>, Set<TagKey<T>>> cache) {
		this.registry = registry;
		this.cache = cache;
	}

	public NestedTagCacheImpl(ResourceKey<? extends Registry<T>> registry) {
		this(registry, new Object2ObjectOpenHashMap<>());

		TagLoaderBuild.EVENT.register(new TagLoaderBuild() {

			@Override
			public <I> void onBuild(String directory, TagEntry.Lookup<I> lookup, DependencySorter<ResourceLocation, TagLoader.SortingEntry> sorter) {

				if (!directory.equals(Registries.tagsDirPath(registry))) {
					return;
				}

				cache.clear();
				sorter.orderByDependencies((id, sorting) -> sorting.entries()
					.stream()
					.map(TagLoader.EntryWithSource::entry)
					.filter(tag -> tag.build(lookup, Consumers.nop()))
					.map(TagEntryAccessor.class::cast)
					.filter(TagEntryAccessor::isTag)
					.map(tag -> TagKey.create(registry, tag.getId()))
					.forEach(tag -> cache
						.computeIfAbsent(TagKey.create(registry, id), k -> new ObjectOpenHashSet<>())
						.add(tag)));

			}

		});

	}

	@Override
	public ResourceKey<? extends Registry<T>> registry() {
		return registry;
	}

	@Override
	public Set<TagKey<T>> getOrEmpty(TagKey<T> tag) {
		return cache.getOrDefault(tag, new ObjectOpenHashSet<>());
	}

	private void send(ServerPlayer recipient) {
		ServerPlayNetworking.send(recipient, new SynchronizeTagCacheS2CPacket<>(this.registry(), this.cache));
	}

	public static void init() {

		PayloadTypeRegistry.playS2C().register(SynchronizeTagCacheS2CPacket.TYPE, SynchronizeTagCacheS2CPacket.CODEC);

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, manager) ->
			GLOBAL.values().forEach(tag -> tag.cache.clear())
		);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
			GLOBAL.values().forEach(tag -> tag.send(player))
		);

	}

}

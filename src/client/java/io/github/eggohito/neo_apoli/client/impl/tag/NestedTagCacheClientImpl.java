package io.github.eggohito.neo_apoli.client.impl.tag;

import io.github.eggohito.neo_apoli.impl.tag.NestedTagCacheImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundTagCacheUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Map;
import java.util.Set;

public class NestedTagCacheClientImpl<T> extends NestedTagCacheImpl<T> {

	private NestedTagCacheClientImpl(ResourceKey<? extends Registry<T>> registry, Map<TagKey<T>, Set<TagKey<T>>> cache) {
		super(registry, cache);
	}

	private static <T> void receive(ClientboundTagCacheUpdatePacket<T> payload) {

		ResourceKey<? extends Registry<T>> registry = payload.registry();
		NestedTagCacheImpl<T> tag = new NestedTagCacheClientImpl<>(registry, payload.cache());

		GLOBAL.put(registry, tag);

	}

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((listener, client) ->
			ClientPlayNetworking.registerReceiver(ClientboundTagCacheUpdatePacket.TYPE, (payload, context) -> receive(payload))
		);

		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> GLOBAL.clear());

	}

}

package io.github.eggohito.neo_apoli.client.tag;

import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdateNestedTagPacket;
import io.github.eggohito.neo_apoli.tag.NestedTag;
import io.github.eggohito.neo_apoli.tag.manager.ServerNestedTagManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNestedTagManager extends ServerNestedTagManager {

	private <T> void receive(ClientboundUpdateNestedTagPacket<T> payload) {
		NestedTag<T> nestedTag = payload.nestedTag();
		this.registry.put(nestedTag.registryKey(), nestedTag);
	}

	private void clear() {
		this.registry.clear();
	}

	public static void init() {

		if (!(INSTANCE instanceof ClientNestedTagManager clientNestedTagManager)) {
			throw new IllegalStateException("Expected '" + ClientNestedTagManager.class.getName() + "', got '" + INSTANCE.getClass().getName() + "'");
		}

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateNestedTagPacket.TYPE, (payload, context) -> clientNestedTagManager.receive(payload));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clientNestedTagManager.clear());

	}

}

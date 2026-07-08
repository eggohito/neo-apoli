package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientActionManager extends ActionManager {

	private ClientActionManager() {

	}

	public static void init() {

	}

	static {

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdatePacket.TYPE, (payload, context) -> {
			actions = ImmutableMap.copyOf(payload.actions());
			tags = ImmutableMap.copyOf(payload.tags());
		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			actions = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}

package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public final class ClientActionManager extends ActionManager {

	private ClientActionManager(Void ignored) {

	}

	public static void init() {

	}

	static {

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			actions = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}

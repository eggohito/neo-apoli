package io.github.eggohito.neo_apoli.client.condition.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public final class ClientConditionManager extends ConditionManager {

	private ClientConditionManager(Void ignored) {

	}

	public static void init() {

	}

	static {
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> conditions = ImmutableMap.of());
	}

}

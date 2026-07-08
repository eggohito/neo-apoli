package io.github.eggohito.neo_apoli.client.condition.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientConditionManager extends ConditionManager {

	private ClientConditionManager() {

	}

	public static void init() {

	}

	static {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdatePacket.TYPE, (payload, context) -> conditions = ImmutableMap.copyOf(payload.conditions()));
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> conditions = ImmutableMap.of());
	}

}

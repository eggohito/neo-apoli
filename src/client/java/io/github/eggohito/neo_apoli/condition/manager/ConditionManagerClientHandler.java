package io.github.eggohito.neo_apoli.condition.manager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;

public final class ConditionManagerClientHandler {

	public static void init() {

	}

	static {
		ClientPlayNetworking.registerGlobalReceiver(ConditionManager.ClientboundUpdatePacket.TYPE, (payload, context) -> ConditionManager.INSTANCE.update(payload.conditions()));
		ClientPlayConnectionEvents.DISCONNECT.register(ConditionManager.ID, (handler, client) -> ConditionManager.INSTANCE.update(Map.of()));
	}

}

package io.github.eggohito.neo_apoli.action.manager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;

public final class ActionManagerClientHandler {

	public static void init() {

	}

	static {
		ClientPlayNetworking.registerGlobalReceiver(ActionManager.ClientboundUpdatePacket.TYPE, (payload, context) -> ActionManager.INSTANCE.update(payload.actions(), payload.tags()));
		ClientPlayConnectionEvents.DISCONNECT.register(ActionManager.ID, (handler, client) -> ActionManager.INSTANCE.update(Map.of(), Map.of()));
	}

}

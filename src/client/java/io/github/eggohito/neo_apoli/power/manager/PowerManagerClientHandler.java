package io.github.eggohito.neo_apoli.power.manager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;

public final class PowerManagerClientHandler {

	public static void init() {

	}

	static {
		ClientPlayNetworking.registerGlobalReceiver(PowerManager.ClientboundUpdatePacket.TYPE, (payload, context) -> PowerManager.INSTANCE.update(payload.powers(), payload.tags()));
		ClientPlayConnectionEvents.DISCONNECT.register(PowerManager.ID, (handler, client) -> PowerManager.INSTANCE.update(Map.of(), Map.of()));
	}

}

package io.github.eggohito.neo_apoli.client.power.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientPowerManager extends PowerManager {

	private ClientPowerManager() {

	}

	public static void init() {

	}

	static {

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdatePacket.TYPE, (payload, context) -> {
			powers = ImmutableMap.copyOf(payload.powers());
			tags = ImmutableMap.copyOf(payload.tags());
		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			powers = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}

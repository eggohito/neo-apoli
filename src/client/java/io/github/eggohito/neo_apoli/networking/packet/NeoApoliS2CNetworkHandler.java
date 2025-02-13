package io.github.eggohito.neo_apoli.networking.packet;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
			ClientPlayNetworking.registerReceiver(SynchronizePowersS2CPacket.ID, NeoApoliS2CNetworkHandler::onSynchronizedPowers);
		});

	}

	private static void onSynchronizedPowers(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power(s) from server!", payload.powers().size());
		PowerManager.receivePayload(payload);
	}

}

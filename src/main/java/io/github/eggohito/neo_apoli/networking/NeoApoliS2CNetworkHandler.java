package io.github.eggohito.neo_apoli.networking;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeDataCommandStorageS2CPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
			ClientPlayNetworking.registerReceiver(SynchronizePowersS2CPacket.ID, NeoApoliS2CNetworkHandler::onPowersSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeDataCommandStorageS2CPacket.ID, NeoApoliS2CNetworkHandler::onDataCommandStorageSynchronized);
		});

	}

	private static void onDataCommandStorageSynchronized(SynchronizeDataCommandStorageS2CPacket payload, ClientPlayNetworking.Context context) {
		((DataCommandStorageHolder) context.client()).neo_apoli$set(payload.id(), payload.nbt());
	}

	private static void onPowersSynchronized(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power(s) from server!", payload.powers().size());
		PowerManager.receiveSyncPayload(payload);
	}

}

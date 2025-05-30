package io.github.eggohito.neo_apoli.networking;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.packet.s2c.*;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
			ClientPlayNetworking.registerReceiver(SynchronizePowersS2CPacket.ID, NeoApoliS2CNetworkHandler::onPowersSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerTagsS2CPacket.ID, NeoApoliS2CNetworkHandler::onPowerTagsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeConditionsS2CPacket.ID, NeoApoliS2CNetworkHandler::onConditionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeActionsS2CPacket.ID, NeoApoliS2CNetworkHandler::onActionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeActionTagsS2CPacket.ID, NeoApoliS2CNetworkHandler::onActionTagsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeDataCommandStorageS2CPacket.ID, NeoApoliS2CNetworkHandler::onDataCommandStorageSynchronized);
		});

	}

	private static void onDataCommandStorageSynchronized(SynchronizeDataCommandStorageS2CPacket payload, ClientPlayNetworking.Context context) {
		((DataCommandStorageHolder) context.client()).neo_apoli$set(payload.id(), payload.nbt());
	}

	private static void onActionTagsSynchronized(SynchronizeActionTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} action tag(s) from server!", payload.actionTags().size());
		ActionManager.receiveSyncTagPayload(payload);
	}

	private static void onActionsSynchronized(SynchronizeActionsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} action(s) from server!", payload.actions().size());
		ActionManager.receiveSyncPayload(payload);
	}

	private static void onConditionsSynchronized(SynchronizeConditionsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} condition(s) from server!", payload.conditions().size());
		ConditionManager.receiveSyncPayload(payload);
	}

	private static void onPowerTagsSynchronized(SynchronizePowerTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power tag(s) from server!", payload.powerTags().size());
		PowerManager.receiveSyncTagPayload(payload);
	}

	private static void onPowersSynchronized(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power(s) from server!", payload.powers().size());
		PowerManager.receiveSyncPayload(payload);
	}

}

package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.keybinding.KeyStateManager;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NeoApoliC2SNetworkHandler {

	public static void init() {

		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			ServerPlayNetworking.registerReceiver(handler, RequestPowerTagsC2SPacket.TYPE, NeoApoliC2SNetworkHandler::onPowerTagsRequest);
			ServerPlayNetworking.registerReceiver(handler, RequestActionTagsC2SPacket.TYPE, NeoApoliC2SNetworkHandler::onActionTagsRequest);
			ServerPlayNetworking.registerReceiver(handler, SynchronizeKeyStatesC2SPacket.TYPE, KeyStateManager::updateStates);
		});

	}

	private static void onActionTagsRequest(RequestActionTagsC2SPacket payload, ServerPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received request for action tags from {}! Sending...", context.player().getName().getString());
		ActionManager.sendTagSyncPayload(context.player());
	}

	private static void onPowerTagsRequest(RequestPowerTagsC2SPacket payload, ServerPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received request for power tags from {}! Sending...", context.player().getName().getString());
		PowerManager.sendTagSyncPayload(context.player());
	}

}

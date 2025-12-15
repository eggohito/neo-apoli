package io.github.eggohito.neo_apoli.client;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.config.NeoApoliClientConfig;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderers;
import io.github.eggohito.neo_apoli.client.integration.PowerIntegrationsClient;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.key.KeyStateManager;
import io.github.eggohito.neo_apoli.network.NeoApoliS2CNetworkHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();
		PowerIntegrationsClient.registerAll();

		NeoApoliAtlases.registerAll();
		HudElementRenderers.registerAll();

		NeoApoliClientConfig.HANDLER.load();

		ClientTickEvents.END_CLIENT_TICK.register(KeyStateManager::startTrackingClient);
		ClientPlayConnectionEvents.DISCONNECT.register(KeyStateManager::stopTrackingClient);

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {

			((CommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setReferencesByDisplayEntry(new Object2ObjectOpenHashMap<>());

			NeoApoli.LOGS.clear();

		});

	}

	public static NeoApoliClientConfig getConfig() {
		return NeoApoliClientConfig.HANDLER.instance();
	}

}

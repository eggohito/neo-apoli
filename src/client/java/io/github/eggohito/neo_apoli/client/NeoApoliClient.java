package io.github.eggohito.neo_apoli.client;

import io.github.eggohito.neo_apoli.client.config.NeoApoliClientConfig;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.HudElementRenderers;
import io.github.eggohito.neo_apoli.client.impl.key.KeyStateClientManagerImpl;
import io.github.eggohito.neo_apoli.client.impl.log.NeoApoliClientLoggerImpl;
import io.github.eggohito.neo_apoli.client.impl.tag.NestedTagCacheClientImpl;
import io.github.eggohito.neo_apoli.client.integration.PowerClientIntegrations;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.NeoApoliS2CNetworkHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();
		PowerClientIntegrations.registerAll();

		NeoApoliAtlases.registerAll();
		HudElementRenderers.registerAll();

		getConfig().loadFromFile();

		KeyStateClientManagerImpl.init();
		NeoApoliClientLoggerImpl.init();
		NestedTagCacheClientImpl.init();

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			((CommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setPowerIdsByIndex(new Int2ObjectOpenHashMap<>());
		});

	}

	public static NeoApoliClientConfig getConfig() {
		return NeoApoliClientConfig.INSTANCE;
	}

}

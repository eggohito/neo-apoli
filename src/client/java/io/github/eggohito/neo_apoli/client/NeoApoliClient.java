package io.github.eggohito.neo_apoli.client;

import io.github.eggohito.neo_apoli.client.action.manager.ClientActionManager;
import io.github.eggohito.neo_apoli.client.condition.manager.ClientConditionManager;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.HudElementRenderers;
import io.github.eggohito.neo_apoli.client.impl.key.KeyStateClientManagerImpl;
import io.github.eggohito.neo_apoli.client.impl.log.NeoApoliClientLoggerImpl;
import io.github.eggohito.neo_apoli.client.impl.tag.NestedTagCacheClientImpl;
import io.github.eggohito.neo_apoli.client.integration.ClientConfigIntegrations;
import io.github.eggohito.neo_apoli.client.integration.PowerClientIntegrations;
import io.github.eggohito.neo_apoli.client.power.manager.ClientPowerManager;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.NeoApoliClientboundPacketListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliClientboundPacketListener.init();

		PowerClientIntegrations.registerAll();
		ClientConfigIntegrations.init();

		NeoApoliAtlases.registerAll();
		HudElementRenderers.registerAll();

		ClientActionManager.init();
		ClientConditionManager.init();
		ClientPowerManager.init();

		KeyStateClientManagerImpl.init();
		NeoApoliClientLoggerImpl.init();
		NestedTagCacheClientImpl.init();

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			((CommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setPowerIdsByIndex(new Int2ObjectOpenHashMap<>());
		});

	}

}

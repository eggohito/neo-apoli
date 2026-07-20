package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.client.action.manager.ClientActionManager;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.HudElementRenderers;
import io.github.eggohito.neo_apoli.client.impl.key.KeyStateClientManagerImpl;
import io.github.eggohito.neo_apoli.client.impl.tag.NestedTagCacheClientImpl;
import io.github.eggohito.neo_apoli.client.integration.ClientConfigIntegrations;
import io.github.eggohito.neo_apoli.client.integration.PowerClientIntegrations;
import io.github.eggohito.neo_apoli.client.power.manager.ClientPowerManager;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManagerClientHandler;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.NeoApoliClientboundPacketListener;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundClearCachedLogsPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliClientboundPacketListener.init();

		PowerClientIntegrations.registerAll();
		ClientConfigIntegrations.init();

		NeoApoliAtlases.registerAll();
		HudElementRenderers.registerAll();

		ClientActionManager.init();
		ConditionManagerClientHandler.init();
		ClientPowerManager.init();

		KeyStateClientManagerImpl.init();
		NestedTagCacheClientImpl.init();

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			((CommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setPowerIdsByIndex(new Int2ObjectOpenHashMap<>());
		});

		ClientPlayNetworking.registerGlobalReceiver(ClientboundClearCachedLogsPacket.TYPE, (payload, context) -> clearCachedLogs());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearCachedLogs());

	}

	public static void clearCachedLogs() {
		NeoApoli.CACHED_LOGS.clear();
	}

}

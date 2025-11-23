package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.client.PowerIntegrationsClient;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingStateHolder;
import io.github.eggohito.neo_apoli.networking.NeoApoliS2CNetworkHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();
		PowerIntegrationsClient.registerAll();

		ClientTickEvents.END_CLIENT_TICK.register(KeyBindingStateHolder::startTrackingClient);
		ClientPlayConnectionEvents.DISCONNECT.register(KeyBindingStateHolder::stopTrackingClient);

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {

			((DataCommandStorageHolder) client).neo_apoli$clear();
			((PowerRecipeDisplayHolder) client).neo_apoli$setReferencesByDisplayEntry(new Object2ObjectOpenHashMap<>());

			NeoApoli.LOGS.clear();

		});

	}

}

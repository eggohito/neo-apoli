package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.networking.packet.NeoApoliS2CNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();

		ClientEntityEvents.ENTITY_LOAD.register((entity, clientWorld) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onAdded(entity)));
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, clientWorld) -> NeoApoliEntityComponents.POWERS.get(entity).getPowers(true).forEach(power -> power.onRemoved(entity)));

	}

}

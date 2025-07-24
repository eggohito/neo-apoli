package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.NeoApoliS2CNetworkHandler;
import io.github.eggohito.neo_apoli.power.Power;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		NeoApoliS2CNetworkHandler.init();

		ClientEntityEvents.ENTITY_LOAD.register((entity, clientWorld) -> PowersComponent.getPowerImpls(entity).forEach(Power.Impl::onAdded));
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, clientWorld) -> PowersComponent.getPowerImpls(entity).forEach(Power.Impl::onRemoved));

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			((DataCommandStorageHolder) client).neo_apoli$clear();
			NeoApoli.LOGS.clear();
		});

	}

}

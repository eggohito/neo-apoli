package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PowerIntegrationsClient {

	public static void registerAll() {

		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> PowersComponent.getAllInstances(entity).forEach(Power.Instance::onAdded));
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> PowersComponent.getAllInstances(entity).forEach(Power.Instance::onRemoved));

		ClientPlayConnectionEvents.DISCONNECT.register(CraftingRecipePower::resetRecipeDisplays);

	}

}

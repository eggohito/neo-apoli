package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PowerIntegrations {

	public static void registerAll() {

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> PowersComponent.getAllInstances(entity).forEach(Power.Instance::onAdded));
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> PowersComponent.getAllInstances(entity).forEach(Power.Instance::onRemoved));

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(ModifyEntityTypeTagPower::resetCache);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ModifyEntityTypeTagPower::sendCache);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(CraftingRecipePower::sendRecipeDisplays);

	}

}

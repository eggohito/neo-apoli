package io.github.eggohito.neo_apoli.power.global.manager;

import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class GlobalPowerSetManagerHandler {

	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(GlobalPowerSetManager.ID, GlobalPowerSetManager.INSTANCE::withContext);
		DependencyManager.GLOBAL_POWER_SETS.register(GlobalPowerSetManager.ID, dependencies -> dependencies.add(PowerManager.ID));

		ReloadableServerResourcesEvents.TAGS_UPDATED.addPhaseOrdering(PowerManager.ID, GlobalPowerSetManager.ID);
		ReloadableServerResourcesEvents.TAGS_UPDATED.register(GlobalPowerSetManager.ID, GlobalPowerSetManager.INSTANCE::finalize);

		ServerEntityEvents.ENTITY_LOAD.addPhaseOrdering(PowerManager.ID, GlobalPowerSetManager.ID);
		ServerEntityEvents.ENTITY_LOAD.register(GlobalPowerSetManager.ID, GlobalPowerSetManager.INSTANCE::grant);

	}

}

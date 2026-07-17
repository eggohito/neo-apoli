package io.github.eggohito.neo_apoli.power.manager;

import com.google.gson.JsonObject;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.PowerPreparation;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class PowerManagerHandler {

	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(PowerManager.ID, PowerManager.INSTANCE::withContext);
		DependencyManager.POWERS.register(PowerManager.ID, dependencies -> dependencies.add(ActionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, PowerManager.ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PowerManager.ID, PowerManager.INSTANCE::send);

		ServerPlayConnectionEvents.INIT.addPhaseOrdering(ActionManager.ID, PowerManager.ID);
		ServerPlayConnectionEvents.INIT.register((handler, server) -> PowerManager.INSTANCE.send(handler.player, false));

		PowerPreparation.EVENT.addPhaseOrdering(PowerManager.ID, MultiplePower.ID);
		PowerPreparation.EVENT.register(PowerManager.ID, (id, jsonWithSource, directoryPath, ops) -> {

			if (jsonWithSource.json() instanceof JsonObject jsonObject) {
				jsonObject.addProperty(PowerHolder.ID_KEY, id.toString());
			}

		});

		PowerPreparation.EVENT.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);
		ReloadableServerResourcesEvents.TAGS_UPDATED.register(PowerManager.ID, PowerManager.INSTANCE::finalize);

	}

}

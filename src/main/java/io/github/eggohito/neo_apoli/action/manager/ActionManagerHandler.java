package io.github.eggohito.neo_apoli.action.manager;

import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class ActionManagerHandler {

	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ActionManager.ID, ActionManager.INSTANCE::withContext);
		DependencyManager.ACTIONS.register(ActionManager.ID, dependencies -> dependencies.add(ConditionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ActionManager.ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ActionManager.ID, ActionManager.INSTANCE::send);

		ServerPlayConnectionEvents.INIT.addPhaseOrdering(ConditionManager.ID, ActionManager.ID);
		ServerPlayConnectionEvents.INIT.register(ActionManager.ID, (handler, server) -> ActionManager.INSTANCE.send(handler.player, false));

	}

}

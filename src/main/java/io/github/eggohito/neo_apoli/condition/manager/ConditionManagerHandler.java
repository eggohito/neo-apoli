package io.github.eggohito.neo_apoli.condition.manager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class ConditionManagerHandler {

	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ConditionManager.ID, ConditionManager.INSTANCE::withContext);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ConditionManager.ID, ConditionManager.INSTANCE::send);
		ServerPlayConnectionEvents.INIT.register(ConditionManager.ID, (handler, server) -> ConditionManager.INSTANCE.send(handler.player, false));

	}

}

package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PowerIntegrations {

	public static void registerAll() {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(ModifyEntityTypeTagPower::resetCache);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ModifyEntityTypeTagPower::sendCache);
	}

}

package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.custom.ModifyPlayerSpawnPower;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class CommonConfigIntegrations {

	public static void init() {

		NeoApoli.getConfig().loadFromFile();

		MultiplePower.Config.INSTANCE.loadFromFile();
		ModifyPlayerSpawnPower.Config.INSTANCE.loadFromFile();

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {

			NeoApoli.getConfig().loadFromFile();

			MultiplePower.Config.INSTANCE.loadFromFile();
			ModifyPlayerSpawnPower.Config.INSTANCE.loadFromFile();

		});

	}

}

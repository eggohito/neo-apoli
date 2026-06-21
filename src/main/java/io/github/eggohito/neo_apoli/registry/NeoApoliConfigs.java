package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.api.event.ConfigCategoryRegistrant;
import io.github.eggohito.neo_apoli.hud.custom.ResourceBarHudElement;
import io.github.eggohito.neo_apoli.power.custom.ModifyPlayerSpawnPower;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;

public final class NeoApoliConfigs {

	public static void registerAll() {
		ConfigCategoryRegistrant.POWER_TYPE.register(MultiplePower.Config.INSTANCE);
		ConfigCategoryRegistrant.POWER_TYPE.register(ModifyPlayerSpawnPower.Config.INSTANCE);
		ConfigCategoryRegistrant.HUD_ELEMENT_TYPE.register(ResourceBarHudElement.Config.INSTANCE);
	}

}

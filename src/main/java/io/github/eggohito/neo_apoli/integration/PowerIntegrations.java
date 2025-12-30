package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraFlightPower;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import io.github.eggohito.neo_apoli.power.custom.TogglePower;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PowerIntegrations {

	public static void registerAll() {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(ModifyEntityTypeTagPower::resetCache);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ModifyEntityTypeTagPower::sendCache);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(CraftingRecipePower::sendRecipeDisplays);
		KeyStateEvents.PRESSED.register(TogglePower::onKeyPressed);
		EntityElytraEvents.CUSTOM.register(ModifyElytraFlightPower::onCustomFlight);
		EntityElytraEvents.ALLOW.register(ModifyElytraFlightPower::allowFlight);
	}

}

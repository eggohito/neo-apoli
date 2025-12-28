package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.client.event.HudElementRenderEvents;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.power.custom.HudRenderPower;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PowerIntegrationsClient {

	public static void registerAll() {
		ClientPlayConnectionEvents.DISCONNECT.register(CraftingRecipePower::resetRecipeDisplays);
		HudElementRenderEvents.PREPARE.register(HudRenderPower::prepareHudElements);
		HudElementRenderEvents.PREPARE.register(CooldownPower::prepareHudElements);
	}

}

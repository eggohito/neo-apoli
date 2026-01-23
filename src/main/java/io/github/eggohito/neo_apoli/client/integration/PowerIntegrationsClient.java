package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.client.event.HudElementRendererEvents;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.power.custom.HudRenderPower;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraRenderPower;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;

public class PowerIntegrationsClient {

	public static void registerAll() {
		ClientPlayConnectionEvents.DISCONNECT.register(CraftingRecipePower::resetRecipeDisplays);
		HudElementRendererEvents.PREPARE.register(HudRenderPower::prepareHudElements);
		HudElementRendererEvents.PREPARE.register(CooldownPower::prepareHudElements);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(ModifyElytraRenderPower::prepareRenderLayer);
	}

}

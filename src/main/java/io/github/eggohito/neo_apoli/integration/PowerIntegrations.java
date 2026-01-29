package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraFlightPower;
import io.github.eggohito.neo_apoli.power.custom.TogglePower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

public class PowerIntegrations {

	public static void registerAll() {
		EntityElytraEvents.ALLOW.register(PowerIntegrations::allowPowerElytraFlight);
		EntityElytraEvents.CUSTOM.register(PowerIntegrations::onPowerElytraFlight);
		KeyStateEvents.PRESSED.register(PowerIntegrations::toggleOnKeyPress);
	}

	private static void toggleOnKeyPress(Player player, KeyState keyState) {

		for (var instance : PowersComponent.getInstances(player, TogglePower.Instance.class)) {

			Context context = instance.createHolderContext();

			if (instance.shouldToggle(context)) {
				instance.toggle(context);
			}

		}

	}

	private static boolean onPowerElytraFlight(LivingEntity entity, boolean tickElytra) {

		Prioritized.InstanceCollection<ModifyElytraFlightPower.Instance> instances = new Prioritized.InstanceCollection<>(entity, ModifyElytraFlightPower.Instance.class);
		Context context = ModifyElytraFlightPower.createContext(entity);

		boolean allow = ModifyElytraFlightPower.modify(context, instances, () -> true);

		if (tickElytra & allow) {
			entity.gameEvent(GameEvent.ELYTRA_GLIDE);
		}

		return allow;

	}

	private static boolean allowPowerElytraFlight(LivingEntity entity) {

		Prioritized.InstanceCollection<ModifyElytraFlightPower.Instance> instances = new Prioritized.InstanceCollection<>(entity, ModifyElytraFlightPower.Instance.class);
		Context context = ModifyElytraFlightPower.createContext(entity);

		return ModifyElytraFlightPower.modify(context, instances, () -> true);

	}

}

package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraFlightPower;
import io.github.eggohito.neo_apoli.power.custom.TogglePower;
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

	private static void toggleOnKeyPress(Player player, KeyState previous, KeyState current) {

		for (var instance : PowersComponent.getInstances(player, TogglePower.Instance.class)) {

			Context context = instance.createHolderContext(player);

			if (instance.shouldToggle(context)) {
				instance.toggle(player, context);
			}

		}

	}

	private static boolean onPowerElytraFlight(LivingEntity entity, boolean tickElytra) {

		try {

			boolean allow = ModifyElytraFlightPower.modify(entity, () -> false);
			if (allow && tickElytra) {
				entity.gameEvent(GameEvent.ELYTRA_GLIDE);
			}

			return allow;

		}

		finally {
			ModifyElytraFlightPower.VISITOR.clear();
		}

	}

	private static boolean allowPowerElytraFlight(LivingEntity entity) {

		try {
			return ModifyElytraFlightPower.modify(entity, () -> true);
		}

		finally {
			ModifyElytraFlightPower.VISITOR.clear();
		}

	}

}

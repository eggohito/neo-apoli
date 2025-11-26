package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.keybinding.KeyState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class KeyBindingEvents {

	public static final Event<Runner> PRESSED = EventFactory.createArrayBacked(
		Runner.class,
		callbacks -> (player, state) -> {

			for (var callback : callbacks) {
				callback.run(player, state);
			}

		}
	);

	public static final Event<Runner> HELD = EventFactory.createArrayBacked(
		Runner.class,
		callbacks -> (player, state) -> {

			for (var callback : callbacks) {
				callback.run(player, state);
			}

		}
	);

	public static final Event<Runner> RELEASED = EventFactory.createArrayBacked(
		Runner.class,
		callbacks -> (player, state) -> {

			for (var callback : callbacks) {
				callback.run(player, state);
			}

		}
	);

	public interface Runner {
		void run(Player player, KeyState state);
	}

}

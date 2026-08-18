package io.github.eggohito.neo_apoli.event;

import io.github.eggohito.neo_apoli.key.KeyState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class KeyStateEvents {

	public static final Event<Pressed> PRESSED = EventFactory.createArrayBacked(
		Pressed.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onPressed(player, previous, current);
			}

		}
	);

	public static final Event<Released> RELEASED = EventFactory.createArrayBacked(
		Released.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onReleased(player, previous, current);
			}

		}
	);

	public static final Event<Held> HELD = EventFactory.createArrayBacked(
		Held.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onHeld(player, previous, current);
			}

		}
	);

	public interface Pressed {
		void onPressed(Player player, KeyState previous, KeyState current);
	}

	public interface Released {
		void onReleased(Player player, KeyState previous, KeyState current);
	}

	public interface Held {
		void onHeld(Player player, KeyState previous, KeyState current);
	}

}

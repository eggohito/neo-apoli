package io.github.eggohito.neo_apoli.event;

import io.github.eggohito.neo_apoli.key.KeyState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public final class KeyStateEvents {

	public static final Event<Pressed> PRESSED = EventFactory.createArrayBacked(
		Pressed.class,
		callbacks -> (player, state) -> {

			for (var callback : callbacks) {
				callback.onPressed(player, state);
			}

		}
	);

	public static final Event<Released> RELEASED = EventFactory.createArrayBacked(
		Released.class,
		callbacks -> (player, state) -> {

			for (var callback : callbacks) {
				callback.onReleased(player, state);
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
		void onPressed(Player player, KeyState state);
	}

	public interface Released {
		void onReleased(Player player, KeyState state);
	}

	public interface Held {
		void onHeld(Player player, KeyState previous, KeyState current);
	}

}

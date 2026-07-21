package io.github.eggohito.neo_apoli.api.event;

import io.github.eggohito.neo_apoli.key.KeyState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class KeyStateEvents {

	public static final Event<Pressed> PRESSED = EventFactory.createArrayBacked(
		Pressed.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onPress(player, previous, current);
			}

		}
	);

	public static final Event<Released> RELEASED = EventFactory.createArrayBacked(
		Released.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onRelease(player, previous, current);
			}

		}
	);

	public static final Event<Held> HELD = EventFactory.createArrayBacked(
		Held.class,
		callbacks -> (player, previous, current) -> {

			for (var callback : callbacks) {
				callback.onHold(player, previous, current);
			}

		}
	);

	public interface Pressed {
		void onPress(Player player, KeyState previous, KeyState current);
	}

	public interface Released {
		void onRelease(Player player, KeyState previous, KeyState current);
	}

	public interface Held {
		void onHold(Player player, KeyState previous, KeyState current);
	}

}

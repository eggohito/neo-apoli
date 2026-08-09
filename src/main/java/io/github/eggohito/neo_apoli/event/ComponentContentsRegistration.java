package io.github.eggohito.neo_apoli.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.ComponentContents;

import java.util.function.Consumer;

/**
 *  An event for registering a custom {@linkplain net.minecraft.network.chat.ComponentContents.Type component contents
 *  type}, used for adding custom text component behaviors.
 */
public interface ComponentContentsRegistration {

	Event<ComponentContentsRegistration> EVENT = EventFactory.createArrayBacked(
		ComponentContentsRegistration.class,
		callbacks -> registrant -> {

			for (var callback : callbacks) {
				callback.register(registrant);
			}

		}
	);

	void register(Consumer<ComponentContents.Type<?>> registrant);

}

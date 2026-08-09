package io.github.eggohito.neo_apoli.client.event;

import io.github.eggohito.neo_apoli.client.util.atlas.AtlasId;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

public class TextureAtlasRegistrationEvents {

	public static final Event<Simple> SIMPLE = EventFactory.createArrayBacked(
		Simple.class,
		callbacks -> registrant -> {

			for (var callback : callbacks) {
				callback.register(registrant);
			}

		}
	);

	public interface Simple {
		void register(Consumer<AtlasId> registrant);
	}

}

package io.github.eggohito.neo_apoli.event;

import io.github.eggohito.neo_apoli.modifier.Modifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ModifierOrderCallback {

	Event<ModifierOrderCallback> EVENT = EventFactory.createArrayBacked(
		ModifierOrderCallback.class,
		callbacks -> orderer -> {

			for (var callback : callbacks) {
				callback.order(orderer);
			}

		}
	);

	void order(Modifier.Orderer orderer);

}

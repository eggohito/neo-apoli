package io.github.eggohito.neo_apoli.api.event;

import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public interface ModifyValue {

	Event<ModifyValue> EVENT = EventFactory.createArrayBacked(
		ModifyValue.class,
		callbacks -> (type, modifiers, original) -> {

			for (var callback : callbacks) {
				callback.beforeModified(type, modifiers, original);
			}

		}
	);

	void beforeModified(Power.Type<?> type, List<Modifier.Operation> modifiers, double original);

}

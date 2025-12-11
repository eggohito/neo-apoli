package io.github.eggohito.neo_apoli.event;

import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public interface ModifyValue {

	Event<ModifyValue> EVENT = EventFactory.createArrayBacked(
		ModifyValue.class,
		callbacks -> (type, modifiers, context, original) -> {

			for (var callback : callbacks) {
				callback.beforeModified(type, modifiers, context, original);
			}

		}
	);

	void beforeModified(PowerType<?> type, List<Modifier.Entry> modifiers, Context context, double original);

}

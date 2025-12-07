package io.github.eggohito.neo_apoli.integration;

import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public interface ModifyValueEvent {

	Event<ModifyValueEvent> INSTANCE = EventFactory.createArrayBacked(
		ModifyValueEvent.class,
		callbacks -> (type, modifiers, context, baseValue) -> {

			for (var callback : callbacks) {
				callback.beforeModified(type, modifiers, context, baseValue);
			}

		}
	);

	void beforeModified(PowerType<?> type, List<Modifier.Entry> modifiers, Context context, double baseValue);

}

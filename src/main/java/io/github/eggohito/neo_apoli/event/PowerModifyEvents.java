package io.github.eggohito.neo_apoli.event;

import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

/**
 *  Events that are invoked before a power type modifies a certain value with modifiers.
 */
public final class PowerModifyEvents {

	public static final Event<NumberValue> NUMBER = EventFactory.createArrayBacked(
		NumberValue.class,
		callbacks -> (type, operations, original) -> {

			for (var callback : callbacks) {
				callback.beforeModified(type, operations, original);
			}

		}
	);

	/**
	 *  An event invoked <b>before</b> a power type modifies a numerical value.
	 */
	public interface NumberValue {
		void beforeModified(Power.Type<?> type, List<Modifier.Operation> operations, double original);
	}

}

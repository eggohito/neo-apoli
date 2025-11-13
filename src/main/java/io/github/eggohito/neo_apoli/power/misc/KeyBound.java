package io.github.eggohito.neo_apoli.power.misc;

import io.github.eggohito.neo_apoli.keybinding.KeyBindingReference;
import io.github.eggohito.neo_apoli.util.context.Context;

/**
 * 	<b>Deprecated</b> in favor of using {@linkplain io.github.eggohito.neo_apoli.condition.custom.key.KeyCondition key conditions}
 */
@Deprecated(forRemoval = true)
public interface KeyBound {

	KeyBindingReference getKey();

	interface Instance {

		default KeyBindingReference getKey() {
			return this.getKeyBound().getKey();
		}

		KeyBound getKeyBound();

		default boolean shouldTrigger(Context context) {
			return true;
		}

		void onPress(Context context);

	}

}

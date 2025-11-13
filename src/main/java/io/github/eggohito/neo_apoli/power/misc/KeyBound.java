package io.github.eggohito.neo_apoli.power.misc;

import io.github.eggohito.neo_apoli.keybinding.KeyBindingReference;
import io.github.eggohito.neo_apoli.util.context.Context;

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

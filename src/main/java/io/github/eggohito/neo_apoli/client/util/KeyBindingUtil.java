package io.github.eggohito.neo_apoli.client.util;

import io.github.eggohito.neo_apoli.client.mixin.accessor.KeyBindingAccessor;
import io.github.eggohito.neo_apoli.util.StringAlias;
import net.minecraft.client.option.KeyBinding;

import java.util.Optional;

public class KeyBindingUtil {

	public static final StringAlias ALIASES = new StringAlias();

	public static Optional<KeyBinding> getKeyBinding(String id) {
		return Optional.ofNullable(KeyBindingAccessor.getKeysById().get(ALIASES.resolveAlias(id)));
	}

}

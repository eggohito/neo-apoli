package io.github.eggohito.neo_apoli.impl.misc;

import net.minecraft.util.context.ContextKeySet;

public interface ContextKeySetHolder {

	default ContextKeySet neo_apoli$getKeySet() {
		throw new AssertionError("Implemented via mixin");
	}

	default void neo_apoli$setKeySet(ContextKeySet keySet) {
		throw new AssertionError("Implemented via mixin");
	}

}

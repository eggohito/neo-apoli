package io.github.eggohito.neo_apoli.util;

import net.minecraft.registry.RegistryWrapper;

public interface Validatable {

	default void validate(RegistryWrapper.WrapperLookup wrapperLookup) {

	}

}

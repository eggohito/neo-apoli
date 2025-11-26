package io.github.eggohito.neo_apoli.util;

import net.minecraft.core.HolderLookup;

public interface Validatable {

	default void validate(HolderLookup.Provider wrapperLookup) {

	}

}

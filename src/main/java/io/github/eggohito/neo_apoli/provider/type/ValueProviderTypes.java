package io.github.eggohito.neo_apoli.provider.type;

import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;

public final class ValueProviderTypes {

	public static void registerAll() {
		NumberProviderTypes.registerAll();
		StringProviderTypes.registerAll();
	}

}

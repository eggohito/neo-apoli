package io.github.eggohito.neo_apoli.provider.type;

import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.ints.IntValueProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.strings.StringValueProviderTypes;

public final class ValueProviderTypes {

	public static void registerAll() {
		DoubleValueProviderTypes.registerAll();
		IntValueProviderTypes.registerAll();
		StringValueProviderTypes.registerAll();
	}

}

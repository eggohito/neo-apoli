package io.github.eggohito.neo_apoli.provider.type;

import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;

public class ValueProviderTypes {

	public static void registerAll() {
		BooleanProviderTypes.registerAll();
		BoxProviderTypes.registerAll();
		NbtProviderTypes.registerAll();
		NumberProviderTypes.registerAll();
		StringProviderTypes.registerAll();
		Vec3ProviderTypes.registerAll();
	}

}

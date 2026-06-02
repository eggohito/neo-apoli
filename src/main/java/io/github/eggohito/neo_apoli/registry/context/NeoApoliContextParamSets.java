package io.github.eggohito.neo_apoli.registry.context;

import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.util.context.ContextKeySet;

public final class NeoApoliContextParamSets {

	public static ContextKeySet any() {

		ContextKeySet.Builder builder = new ContextKeySet.Builder();
		NeoApoliRegistries.CONTEXT_PARAMETER.forEach(builder::optional);

		return builder.build();

	}

	public static void registerAll() {

	}

}

package io.github.eggohito.neo_apoli.registry;

import net.minecraft.Util;
import net.minecraft.util.context.ContextKeySet;

public class NeoApoliContextParamSets {

	public static final ContextKeySet ANY = Util
		.make(new ContextKeySet.Builder(), builder -> NeoApoliRegistries.CONTEXT_PARAMETER.forEach(builder::optional))
		.build();

	public static void registerAll() {

	}

}

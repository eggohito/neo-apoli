package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

public class NeoApoliArgumentTypes {

	public static void registerAll() {

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power"), PowerArgumentType.class, new PowerArgumentType.Info());

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgumentType.class, new ActionArgumentType.Info());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgumentType.class, new ConditionArgumentType.Info());

	}

}

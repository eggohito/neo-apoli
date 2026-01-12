package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

public class NeoApoliArguments {

	public static void registerAll() {
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power"), PowerArgument.class, new PowerArgument.Info());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgument.class, new ActionArgument.Info());
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgument.class, new ConditionArgument.Info());
	}

}

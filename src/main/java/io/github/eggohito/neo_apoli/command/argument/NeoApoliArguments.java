package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

public class NeoApoliArguments {

	public static void registerAll() {
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power"), PowerArgument.class, PowerArgument.Info.INSTANCE);
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgument.class, ActionArgument.Info.INSTANCE);
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgument.class, ConditionArgument.Info.INSTANCE);
	}

}

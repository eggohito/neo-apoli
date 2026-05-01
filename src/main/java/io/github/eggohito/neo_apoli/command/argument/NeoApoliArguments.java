package io.github.eggohito.neo_apoli.command.argument;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.command.argument.action.ActionArgument;
import io.github.eggohito.neo_apoli.command.argument.action.ActionKindArgument;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionArgument;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionKindArgument;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public class NeoApoliArguments {

	public static void registerAll() {

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("power"), PowerArgument.class, PowerArgument.Info.INSTANCE);
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action"), ActionArgument.class, ActionArgument.Info.INSTANCE);
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition"), ConditionArgument.class, ConditionArgument.Info.INSTANCE);

		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("action_category"), ActionKindArgument.class, SingletonArgumentInfo.contextFree(ActionKindArgument::new));
		ArgumentTypeRegistry.registerArgumentType(NeoApoli.id("condition_category"), ConditionKindArgument.class, SingletonArgumentInfo.contextFree(ConditionKindArgument::new));

	}

}

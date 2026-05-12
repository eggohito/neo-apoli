package io.github.eggohito.neo_apoli.registry.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.command.argument.action.ActionArgument;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class NeoApoliActionKinds {

	public static void registerAll() {
		register(BiEntityAction.Kind.INSTANCE);
		register(BlockAction.Kind.INSTANCE);
		register(EntityAction.Kind.INSTANCE);
		register(ItemAction.Kind.INSTANCE);
		register(Action.Kind.INSTANCE);
	}

	public static <A extends Action, K extends Action.Kind<A>> K register(K kind) {
		return Registry.register(NeoApoliRegistries.ACTION_KIND, kind.registryKey().location(), kind);
	}

	public static ArgumentBuilder<CommandSourceStack, ?> addAsArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {

		for (var kind : NeoApoliRegistries.ACTION_KIND) {

			String kindId = kind.registryKey().location().toString();
			var commandBuilder = kind.commandBuilder();

			if (commandBuilder == null) {
				continue;
			}

			Consumer<String> finalizer = key -> builder
				.then(literal(kindId)
					.then(argument(key, ActionArgument.inlineAction(buildContext, kind))
						.then(commandBuilder.apply(key).addArguments(buildContext, literal("with")))));

			finalizer.accept("action");

		}

		return builder;

	}

}

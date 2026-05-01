package io.github.eggohito.neo_apoli.action.kind;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.kind.custom.BiEntityActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.BlockActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.EntityActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.ItemActionKind;
import io.github.eggohito.neo_apoli.command.argument.action.ActionArgument;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ActionKinds {

	public static void registerAll() {
		register(BiEntityActionKind.INSTANCE);
		register(BlockActionKind.INSTANCE);
		register(EntityActionKind.INSTANCE);
		register(ItemActionKind.INSTANCE);
		register(ActionKind.INSTANCE);
	}

	public static <A extends Action, C extends ActionKind<A>> C register(C category) {
		return Registry.register(NeoApoliRegistries.ACTION_KIND, category.registryKey().location(), category);
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

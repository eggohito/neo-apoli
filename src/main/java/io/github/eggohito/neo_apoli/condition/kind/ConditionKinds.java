package io.github.eggohito.neo_apoli.condition.kind;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.kind.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ConditionKinds {

	public static void registerAll() {
		register(BiEntityConditionKind.INSTANCE);
		register(BlockConditionKind.INSTANCE);
		register(DamageConditionKind.INSTANCE);
		register(EffectConditionKind.INSTANCE);
		register(EntityConditionKind.INSTANCE);
		register(FluidConditionKind.INSTANCE);
		register(ItemConditionKind.INSTANCE);
		register(WorldConditionKind.INSTANCE);
	}

	public static <C extends Condition, K extends ConditionKind<C>> K register(K kind) {
		return Registry.register(NeoApoliRegistries.CONDITION_KIND, kind.registryKey().location(), kind);
	}

	public static ArgumentBuilder<CommandSourceStack, ?> addAsArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {

		for (var kind : NeoApoliRegistries.CONDITION_KIND) {

			String kindId = kind.registryKey().location().toString();
			Function<String, ConditionKind.CommandBuilder> commandBuilder = kind.commandBuilder();

			if (commandBuilder == null) {
				continue;
			}

			Consumer<String> finalizer = key -> builder
				.then(literal(kindId)
					.then(argument(key, ConditionArgument.inlineCondition(buildContext, kind))
						.then(commandBuilder.apply(key).addArguments(rootNode, buildContext, literal("with"), positive))));

			finalizer.accept("condition");

		}

		return builder;

	}

}

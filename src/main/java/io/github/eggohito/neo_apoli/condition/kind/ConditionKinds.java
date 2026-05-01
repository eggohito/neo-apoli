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

	public static <C extends Condition, CC extends ConditionKind<C>> CC register(CC category) {
		return Registry.register(NeoApoliRegistries.CONDITION_KIND, category.registryKey().location(), category);
	}

	public static ArgumentBuilder<CommandSourceStack, ?> addAsArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {

		for (var category : NeoApoliRegistries.CONDITION_KIND) {

			String categoryId = category.registryKey().toString();
			Function<String, ConditionKind.CommandBuilder> commandBuilder = category.commandBuilder();

			if (commandBuilder == null) {
				continue;
			}

			Consumer<String> finalizer = key -> builder
				.then(literal(categoryId)
					.then(literal("with")
						.then(commandBuilder.apply(key).addArguments(rootNode, buildContext, argument(key, ConditionArgument.inlineCondition(buildContext, category)), positive))));

			finalizer.accept("condition");

		}

		return builder;

	}

}

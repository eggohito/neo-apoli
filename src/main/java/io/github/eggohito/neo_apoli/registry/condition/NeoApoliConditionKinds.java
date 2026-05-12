package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.eggohito.neo_apoli.command.argument.condition.ConditionArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.effect.EffectCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class NeoApoliConditionKinds {

	public static void registerAll() {
		register(BiEntityCondition.Kind.INSTANCE);
		register(BlockCondition.Kind.INSTANCE);
		register(DamageCondition.Kind.INSTANCE);
		register(EffectCondition.Kind.INSTANCE);
		register(EntityCondition.Kind.INSTANCE);
		register(FluidCondition.Kind.INSTANCE);
		register(ItemCondition.Kind.INSTANCE);
		register(WorldCondition.Kind.INSTANCE);
	}

	public static <C extends Condition, K extends Condition.Kind<C>> K register(K kind) {
		return Registry.register(NeoApoliRegistries.CONDITION_KIND, kind.registryKey().location(), kind);
	}

	public static ArgumentBuilder<CommandSourceStack, ?> addAsArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {

		for (var kind : NeoApoliRegistries.CONDITION_KIND) {

			String kindId = kind.registryKey().location().toString();
			Function<String, Condition.Kind.CommandBuilder> commandBuilder = kind.commandBuilder();

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

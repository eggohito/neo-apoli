package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public enum WorldConditionKind implements ConditionKind<WorldCondition> {

	INSTANCE;

	@Override
	public @NotNull Function<String, CommandBuilder> commandBuilder() {
		return conditionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
				return this.optionallyAddForkedConditionedLogic(rootNode, builder, positive, this::test);
			}

			boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
				return WorldConditionKind.this.test(
					commandContext,
					conditionKey,
					condition -> Util.getRegisteredName(NeoApoliRegistries.WORLD_CONDITION_TYPE, condition.getType()),
					FailableFunction.identity()
				);
			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<WorldCondition>> registryKey() {
		return NeoApoliRegistryKeys.WORLD_CONDITION;
	}

	@Override
	public Codec<WorldCondition> codec() {
		return WorldCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Item condition";
	}

}

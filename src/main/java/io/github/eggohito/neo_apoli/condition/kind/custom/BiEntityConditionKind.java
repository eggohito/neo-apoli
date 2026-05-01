package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public enum BiEntityConditionKind implements ConditionKind<BiEntityCondition> {

	INSTANCE;

	@Override
	public @NotNull Function<String, CommandBuilder> commandBuilder() {
		return conditionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
				return builder
					.then(argument("actor", EntityArgument.entity())
						.then(this.optionallyAddForkedConditionedLogic(rootNode, argument("target", EntityArgument.entity()), positive, this::test)));
			}

			boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
				return BiEntityConditionKind.this.test(
					commandContext,
					conditionKey,
					condition -> Util.getRegisteredName(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, condition.getType()),
					builder -> builder
						.withRequired(NeoApoliContextParams.ACTOR_ENTITY, EntityArgument.getEntity(commandContext, "actor"))
						.withRequired(NeoApoliContextParams.TARGET_ENTITY, EntityArgument.getEntity(commandContext, "target"))
				);
			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<BiEntityCondition>> registryKey() {
		return NeoApoliRegistryKeys.BIENTITY_CONDITION;
	}

	@Override
	public Codec<BiEntityCondition> codec() {
		return BiEntityCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Bi-entity condition";
	}

}

package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public enum FluidConditionKind implements ConditionKind<FluidCondition> {

	INSTANCE;

	@Override
	public @NotNull Function<String, CommandBuilder> commandBuilder() {
		return conditionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
				return builder
					.then(this.optionallyAddForkedConditionedLogic(rootNode, argument("pos", BlockPosArgument.blockPos()), positive, this::test));
			}

			boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

				ServerLevel serverLevel = commandContext.getSource().getLevel();
				BlockPos pos = BlockPosArgument.getLoadedBlockPos(commandContext, "pos");

				return FluidConditionKind.this.test(
					commandContext,
					conditionKey,
					condition -> Util.getRegisteredName(NeoApoliRegistries.FLUID_CONDITION_TYPE, condition.getType()),
					builder -> builder.withRequired(NeoApoliContextParams.FLUID_STATE, serverLevel.getFluidState(pos))
				);

			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<FluidCondition>> registryKey() {
		return NeoApoliRegistryKeys.FLUID_CONDITION;
	}

	@Override
	public Codec<FluidCondition> codec() {
		return FluidCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Fluid condition";
	}

}

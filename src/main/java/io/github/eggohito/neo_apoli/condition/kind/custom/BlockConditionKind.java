package io.github.eggohito.neo_apoli.condition.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
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

public enum BlockConditionKind implements ConditionKind<BlockCondition> {

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
				BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(commandContext, "pos");

				return BlockConditionKind.this.test(
					commandContext,
					conditionKey,
					condition -> Util.getRegisteredName(NeoApoliRegistries.BLOCK_CONDITION_TYPE, condition.getType()),
					builder -> builder
						.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
						.withRequired(NeoApoliContextParams.BLOCK_STATE, serverLevel.getBlockState(blockPos))
						.withNullable(NeoApoliContextParams.BLOCK_ENTITY, serverLevel.getBlockEntity(blockPos))
				);

			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<BlockCondition>> registryKey() {
		return NeoApoliRegistryKeys.BLOCK_CONDITION;
	}

	@Override
	public Codec<BlockCondition> codec() {
		return BlockCondition.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Block condition";
	}

}

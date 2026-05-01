package io.github.eggohito.neo_apoli.action.kind.custom;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
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

import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public enum BlockActionKind implements ActionKind<BlockAction> {

	INSTANCE;

	@Override
	public Function<String, CommandBuilder> commandBuilder() {
		return actionKey -> new CommandBuilder() {

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {
				return builder
					.then(argument("pos", BlockPosArgument.blockPos())
						.executes(this::execute));
			}

			int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

				ServerLevel level = commandContext.getSource().getLevel();
				BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(commandContext, "pos");

				return BlockActionKind.this.execute(
					commandContext,
					actionKey,
					action -> Util.getRegisteredName(NeoApoliRegistries.BLOCK_ACTION_TYPE, action.getType()),
					builder -> builder
						.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
						.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
						.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
				);

			}

		};
	}

	@Override
	public ResourceKey<? extends Registry<BlockAction>> registryKey() {
		return NeoApoliRegistryKeys.BLOCK_ACTION;
	}

	@Override
	public Codec<BlockAction> codec() {
		return BlockAction.CODEC;
	}

	@Override
	public String asDisplayString() {
		return "Block action";
	}

}

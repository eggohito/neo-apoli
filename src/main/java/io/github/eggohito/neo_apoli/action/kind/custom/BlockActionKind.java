package io.github.eggohito.neo_apoli.action.kind.custom;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public enum BlockActionKind implements ActionKind<BlockAction> {

	INSTANCE;

	@Override
	public Function<String, CommandBuilder> commandBuilder() {
		return actionKey -> new CommandBuilder() {

			private static final SuggestionProvider<CommandSourceStack> DIRECTION_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(Direction.class.getEnumConstants()).map(e -> e.toString().toLowerCase(Locale.ROOT)), builder);

			@Override
			public ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {
				return builder
					.then(argument("pos", BlockPosArgument.blockPos())
						.executes(this::executeWithoutDirection)
						.then(argument("direction", StringArgumentType.word())
							.suggests(DIRECTION_SUGGESTIONS)
							.executes(this::executeWithDirection)));
			}

			int executeWithoutDirection(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
				return this.execute(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), null);
			}

			int executeWithDirection(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {

				String directionName = StringArgumentType.getString(commandContext, "direction").toLowerCase(Locale.ROOT);
				Direction direction = Direction.CODEC.parse(JavaOps.INSTANCE, directionName).getOrThrow(error -> MiscUtil.createCommandException(() -> error));

				return this.execute(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), direction);

			}

			int execute(CommandContext<CommandSourceStack> commandContext, BlockPos blockPos, @Nullable Direction direction) throws CommandSyntaxException {
				ServerLevel level = commandContext.getSource().getLevel();
				return BlockActionKind.this.execute(
					commandContext,
					actionKey,
					action -> Util.getRegisteredName(NeoApoliRegistries.BLOCK_ACTION_TYPE, action.getType()),
					builder -> builder
						.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
						.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
						.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
						.withNullable(NeoApoliContextParams.DIRECTION, direction)
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

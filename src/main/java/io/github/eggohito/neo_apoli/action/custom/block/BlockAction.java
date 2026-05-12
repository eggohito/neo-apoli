package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public interface BlockAction extends Action {

	Codec<BlockAction> CODEC = Codec.recursive(BlockAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(BlockAction::getType, Type::mapCodec), codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, BlockAction> STREAM_CODEC = Type.STREAM_CODEC.dispatch(BlockAction::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS);
	}

	enum Kind implements Action.Kind<BlockAction> {

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
					return Kind.this.execute(
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

	record Type<A extends BlockAction>(MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) implements Action.Type<A> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.BLOCK_ACTION_TYPE, Action.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);

		@Override
		public BlockAction.Kind kind() {
			return BlockAction.Kind.INSTANCE;
		}

	}

}

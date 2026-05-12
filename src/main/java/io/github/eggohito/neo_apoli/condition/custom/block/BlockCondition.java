package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public interface BlockCondition extends Condition {

	Codec<BlockCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(BlockCondition::getType, Type::mapCodec), ConstantBlockCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BlockCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(BlockCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS, NeoApoliContextParams.BLOCK_STATE);
	}

	enum Kind implements Condition.Kind<BlockCondition> {

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

					return Kind.this.test(
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

	record Type<C extends BlockCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.BLOCK_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BLOCK_CONDITION_TYPE);

		@Override
		public BlockCondition.Kind kind() {
			return BlockCondition.Kind.INSTANCE;
		}

	}

}

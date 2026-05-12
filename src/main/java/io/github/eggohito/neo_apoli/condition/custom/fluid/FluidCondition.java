package io.github.eggohito.neo_apoli.condition.custom.fluid;

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

public interface FluidCondition extends Condition {

	Codec<FluidCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(FluidCondition::getType, Type::mapCodec), ConstantFluidCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, FluidCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(FluidCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.FLUID_STATE);
	}

	enum Kind implements Condition.Kind<FluidCondition> {

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

					return Kind.this.test(
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

	record Type<C extends FluidCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.FLUID_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.FLUID_CONDITION_TYPE);

		@Override
		public FluidCondition.Kind kind() {
			return FluidCondition.Kind.INSTANCE;
		}

	}

}

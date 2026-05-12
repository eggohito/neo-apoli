package io.github.eggohito.neo_apoli.condition.custom.world;

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
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.function.FailableFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public interface WorldCondition extends Condition {

	Codec<WorldCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(WorldCondition::getType, Type::mapCodec), ConstantWorldCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, WorldCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(WorldCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	enum Kind implements Condition.Kind<WorldCondition> {

		INSTANCE;

		@Override
		public @NotNull Function<String, CommandBuilder> commandBuilder() {
			return conditionKey -> new CommandBuilder() {

				@Override
				public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
					return this.optionallyAddForkedConditionedLogic(rootNode, builder, positive, this::test);
				}

				boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
					return Kind.this.test(
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

	record Type<C extends WorldCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.WORLD_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.WORLD_CONDITION_TYPE);

		@Override
		public WorldCondition.Kind kind() {
			return WorldCondition.Kind.INSTANCE;
		}

	}

}

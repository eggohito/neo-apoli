package io.github.eggohito.neo_apoli.condition.custom.bientity;

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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public interface BiEntityCondition extends Condition {

	Codec<BiEntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BiEntityCondition.Type.CODEC.dispatch(BiEntityCondition::getType, BiEntityCondition.Type::mapCodec), ConstantBiEntityCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BiEntityCondition> STREAM_CODEC = BiEntityCondition.Type.STREAM_CODEC.dispatch(BiEntityCondition::getType, BiEntityCondition.Type::streamCodec);

	@Override
	BiEntityCondition.Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ACTOR_ENTITY, NeoApoliContextParams.TARGET_ENTITY);
	}

	enum Kind implements Condition.Kind<BiEntityCondition> {

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
					return Kind.this.test(
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

	record Type<C extends BiEntityCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);

		@Override
		public BiEntityCondition.Kind kind() {
			return BiEntityCondition.Kind.INSTANCE;
		}

	}

}

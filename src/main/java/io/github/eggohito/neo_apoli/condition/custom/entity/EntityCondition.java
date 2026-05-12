package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
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
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public interface EntityCondition extends Condition {

	Codec<EntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(EntityCondition::getType, Type::mapCodec), ConstantEntityCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EntityCondition> STREAM_CODEC = Type.STREAM_CODEC.dispatch(EntityCondition::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY);
	}

	enum Kind implements Condition.Kind<EntityCondition> {

		INSTANCE;

		@Override
		public @NotNull Function<String, CommandBuilder> commandBuilder() {
			return conditionKey -> new CommandBuilder() {

				@Override
				public ArgumentBuilder<CommandSourceStack, ?> addArguments(Optional<CommandNode<CommandSourceStack>> rootNode, CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder, boolean positive) {
					return builder
						.then(this.optionallyAddForkedConditionedLogic(rootNode, argument("target", EntityArgument.entity()), positive, this::test));
				}

				boolean test(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
					Entity entity = EntityArgument.getEntity(commandContext, "target");
					return Kind.this.test(
						commandContext,
						conditionKey,
						condition -> Util.getRegisteredName(NeoApoliRegistries.ENTITY_CONDITION_TYPE, condition.getType()),
						builder -> builder
							.withRequired(NeoApoliContextParams.THIS_ENTITY, entity)
							.withRequired(NeoApoliContextParams.THIS_POS, entity.position())
					);
				}

			};
		}

		@Override
		public ResourceKey<? extends Registry<EntityCondition>> registryKey() {
			return NeoApoliRegistryKeys.ENTITY_CONDITION;
		}

		@Override
		public Codec<EntityCondition> codec() {
			return EntityCondition.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Entity condition";
		}

	}

	record Type<C extends EntityCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements Condition.Type<C> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ENTITY_CONDITION_TYPE, Condition.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);

		@Override
		public EntityCondition.Kind kind() {
			return EntityCondition.Kind.INSTANCE;
		}

	}

}

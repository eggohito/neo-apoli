package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
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
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;

public interface EntityAction extends Action {

	Codec<EntityAction> CODEC = Codec.recursive(EntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(EntityAction::getType, Type::mapCodec), codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, EntityAction> STREAM_CODEC = Type.STREAM_CODEC.dispatch(EntityAction::getType, Type::streamCodec);

	@Override
	Type<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY);
	}

	enum Kind implements Action.Kind<EntityAction> {

		INSTANCE;

		@Override
		public Function<String, CommandBuilder> commandBuilder() {
			return actionKey -> new CommandBuilder() {

				@Override
				public ArgumentBuilder<CommandSourceStack, ?> addArguments(CommandBuildContext buildContext, ArgumentBuilder<CommandSourceStack, ?> builder) {
					return builder
						.then(argument("target", EntityArgument.entity())
							.executes(this::execute));
				}

				int execute(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
					Entity entity = EntityArgument.getEntity(commandContext, "target");
					return Kind.this.execute(
						commandContext,
						actionKey,
						action -> Util.getRegisteredName(NeoApoliRegistries.ENTITY_ACTION_TYPE, action.getType()),
						builder -> builder
							.withRequired(NeoApoliContextParams.THIS_ENTITY, entity)
							.withRequired(NeoApoliContextParams.THIS_POS, entity.position())
					);
				}

			};
		}

		@Override
		public ResourceKey<? extends Registry<EntityAction>> registryKey() {
			return NeoApoliRegistryKeys.ENTITY_ACTION;
		}

		@Override
		public Codec<EntityAction> codec() {
			return EntityAction.CODEC;
		}

		@Override
		public String asDisplayString() {
			return "Entity action";
		}

	}

	record Type<A extends EntityAction>(MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) implements Action.Type<A> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ENTITY_ACTION_TYPE, Action.Type.ALIASES);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);

		@Override
		public EntityAction.Kind kind() {
			return EntityAction.Kind.INSTANCE;
		}

	}

}

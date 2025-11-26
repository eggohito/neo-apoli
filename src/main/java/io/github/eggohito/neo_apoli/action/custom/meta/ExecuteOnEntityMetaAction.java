package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public interface ExecuteOnEntityMetaAction extends MetaAction {

	EntityAction action();

	EntityTarget entity();

	@Override
	default void execute(Context context) {

		Optional<Entity> entity = context.optional(entity().getParameter());
		Context entityContext = ContextImpl.of(context, builder -> builder
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.addOptional(NeoApoliContextKeys.THIS_ENTITY, entity)
			.addOptional(NeoApoliContextKeys.ENTITY_POS, entity.map(Entity::position)));

		action().execute(entityContext.makeChild(".action"));

	}

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

	@Override
	default void validate(ProblemReporter reporter) {
		MetaAction.super.validate(reporter);
		action().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.forChild(".action"));
	}

	static <M extends ExecuteOnEntityMetaAction> MapCodec<M> createCodec(BiFunction<EntityAction, EntityTarget, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityAction.CODEC.fieldOf("action").forGetter(ExecuteOnEntityMetaAction::action),
			EntityTarget.CODEC.fieldOf("entity").forGetter(ExecuteOnEntityMetaAction::entity)
		).apply(instance, constructor));
	}

	static <M extends ExecuteOnEntityMetaAction> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BiFunction<EntityAction, EntityTarget, M> constructor) {
		return StreamCodec.composite(
			EntityAction.STREAM_CODEC, ExecuteOnEntityMetaAction::action,
			EntityTarget.STREAM_CODEC, ExecuteOnEntityMetaAction::entity,
			constructor
		);
	}

}

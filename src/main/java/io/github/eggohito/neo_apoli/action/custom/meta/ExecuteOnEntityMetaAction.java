package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

public interface ExecuteOnEntityMetaAction extends MetaAction {

	EntityAction action();

	TypedContextKey<Entity> entity();

	@Override
	default void execute(Context context) {

		if (!context.hasParameter(entity())) {
			return;
		}

		Entity entity = context.required(entity());
		Context actionContext = new Context.Builder(context)
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());

		action().execute(actionContext.forChild(".action"));

	}

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	default void validate(ProblemReporter reporter) {
		MetaAction.super.validate(reporter);
		action().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.forChild(".action"));
	}

	static <M extends ExecuteOnEntityMetaAction> MapCodec<M> createCodec(BiFunction<EntityAction, TypedContextKey<Entity>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityAction.CODEC.fieldOf("action").forGetter(ExecuteOnEntityMetaAction::action),
			NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(ExecuteOnEntityMetaAction::entity)
		).apply(instance, constructor));
	}

	static <M extends ExecuteOnEntityMetaAction> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BiFunction<EntityAction, TypedContextKey<Entity>, M> constructor) {
		return StreamCodec.composite(
			EntityAction.STREAM_CODEC, ExecuteOnEntityMetaAction::action,
			NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, ExecuteOnEntityMetaAction::entity,
			constructor
		);
	}

}

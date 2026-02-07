package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

public interface IExecuteOnEntityMetaAction extends MetaAction {

	EntityAction action();

	ContextParameter<Entity> entity();

	@Override
	default void execute(Context context) {

		if (!context.hasParameter(entity())) {
			return;
		}

		Entity entity = context.getRequired(entity());
		Context actionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.THIS_ENTITY, entity)
			.withRequired(NeoApoliContextParams.THIS_POS, entity.position())
			.build(entity.level());

		action().execute(actionContext.forChild(".action"));

	}

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	default void validate(Context.Validator validator) {
		MetaAction.super.validate(validator);
		action().validate(validator
			.withAdditionalKeysFromSets()
			.forChild(".action"));
	}

	static <M extends IExecuteOnEntityMetaAction> MapCodec<M> mapCodec(BiFunction<EntityAction, ContextParameter<Entity>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityAction.CODEC.fieldOf("action").forGetter(IExecuteOnEntityMetaAction::action),
			NeoApoliCodecs.ENTITY_CONTEXT_PARAM.fieldOf("entity").forGetter(IExecuteOnEntityMetaAction::entity)
		).apply(instance, constructor));
	}

	static <M extends IExecuteOnEntityMetaAction> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(BiFunction<EntityAction, ContextParameter<Entity>, M> constructor) {
		return StreamCodec.composite(
			EntityAction.STREAM_CODEC, IExecuteOnEntityMetaAction::action,
			NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, IExecuteOnEntityMetaAction::entity,
			constructor
		);
	}

}

package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

public interface ExecuteOnEntityMetaAction extends Action {

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
		Action.super.validate(validator);
		action().validate(validator
			.withAdditionalKeysFromSets()
			.forChild(".action"));
	}

	static <M extends ExecuteOnEntityMetaAction> MapCodec<M> mapCodec(BiFunction<EntityAction, ContextParameter<Entity>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityAction.CODEC.fieldOf("action").forGetter(ExecuteOnEntityMetaAction::action),
			NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(ExecuteOnEntityMetaAction::entity)
		).apply(instance, constructor));
	}

	static <M extends ExecuteOnEntityMetaAction> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(BiFunction<EntityAction, ContextParameter<Entity>, M> constructor) {
		return StreamCodec.composite(
			EntityAction.STREAM_CODEC, ExecuteOnEntityMetaAction::action,
			NeoApoliContextParams.StreamCodecs.ENTITY, ExecuteOnEntityMetaAction::entity,
			constructor
		);
	}

}

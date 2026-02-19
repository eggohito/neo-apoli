package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

public interface ITestEntityMetaCondition extends MetaCondition {

	ContextKeySet DEFAULT_CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.THIS_ENTITY)
		.required(NeoApoliContextParams.THIS_POS)
		.build();

	EntityCondition condition();

	ContextParameter<Entity> entity();

	@Override
	default boolean test(Context context) {

		if (!context.hasParameter(entity())) {
			return false;
		}

		Entity entity = context.getRequired(entity());
		Context conditionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.THIS_ENTITY, entity)
			.withRequired(NeoApoliContextParams.THIS_POS, entity.position())
			.build(entity.level());

		return condition().test(conditionContext.forChild(".condition"));

	}

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	default void validate(Context.Validator validator) {
		MetaCondition.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(DEFAULT_CONDITION_CONTEXT).forChild(".condition"));
	}

	static <M extends ITestEntityMetaCondition> MapCodec<M> mapCodec(BiFunction<EntityCondition, ContextParameter<Entity>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityCondition.CODEC.fieldOf("condition").forGetter(ITestEntityMetaCondition::condition),
			NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(ITestEntityMetaCondition::entity)
		).apply(instance, constructor));
	}

	static <M extends ITestEntityMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(BiFunction<EntityCondition, ContextParameter<Entity>, M> constructor) {
		return StreamCodec.composite(
			EntityCondition.STREAM_CODEC, ITestEntityMetaCondition::condition,
			NeoApoliContextParams.StreamCodecs.ENTITY, ITestEntityMetaCondition::entity,
			constructor
		);
	}

}

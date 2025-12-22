package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
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

public interface ITestEntityMetaCondition extends MetaCondition {

	EntityCondition condition();

	TypedContextKey<Entity> entity();

	@Override
	default boolean test(Context context) {

		if (!context.hasParameter(entity())) {
			return false;
		}

		Entity entity = context.required(entity());
		Context conditionContext = new Context.Builder(context)
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
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
		condition().validate(validator
			.withKeySet(ContextKeySetHelper.merge(validator.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.forChild(".condition"));
	}

	static <M extends ITestEntityMetaCondition> MapCodec<M> createCodec(BiFunction<EntityCondition, TypedContextKey<Entity>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityCondition.CODEC.fieldOf("condition").forGetter(ITestEntityMetaCondition::condition),
			NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(ITestEntityMetaCondition::entity)
		).apply(instance, constructor));
	}

	static <M extends ITestEntityMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BiFunction<EntityCondition, TypedContextKey<Entity>, M> constructor) {
		return StreamCodec.composite(
			EntityCondition.STREAM_CODEC, ITestEntityMetaCondition::condition,
			NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, ITestEntityMetaCondition::entity,
			constructor
		);
	}

}

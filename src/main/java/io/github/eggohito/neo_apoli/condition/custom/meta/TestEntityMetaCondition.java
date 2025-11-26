package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.*;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public interface TestEntityMetaCondition extends MetaCondition {

	EntityCondition condition();

	EntityTarget entity();

	@Override
	default boolean test(Context context) {

		Optional<Entity> entity = context.optional(entity().getParameter());
		Context conditionContext = ContextImpl.of(context, builder -> builder
			.addOptional(NeoApoliContextKeys.THIS_ENTITY, entity)
			.addOptional(NeoApoliContextKeys.ENTITY_POS, entity.map(Entity::position)));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	default void validate(ProblemReporter reporter) {
		condition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.forChild(".condition"));
	}

	static <M extends TestEntityMetaCondition> MapCodec<M> createCodec(BiFunction<EntityCondition, EntityTarget, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityCondition.CODEC.fieldOf("condition").forGetter(TestEntityMetaCondition::condition),
			EntityTarget.CODEC.fieldOf("entity").forGetter(TestEntityMetaCondition::entity)
		).apply(instance, constructor));
	}

	static <M extends TestEntityMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(BiFunction<EntityCondition, EntityTarget, M> constructor) {
		return StreamCodec.composite(
			EntityCondition.STREAM_CODEC, TestEntityMetaCondition::condition,
			EntityTarget.STREAM_CODEC, TestEntityMetaCondition::entity,
			constructor
		);
	}

}

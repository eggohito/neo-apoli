package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;
import java.util.function.BiFunction;

public interface TestEntityMetaCondition extends MetaCondition {

	EntityCondition condition();

	EntityTarget entity();

	@Override
	default boolean test(Context context) {

		Optional<Entity> entity = context.optional(entity().getParameter());
		Context conditionContext = ContextImpl.of(context, builder -> builder
			.addOptional(NeoApoliContextParameters.THIS_ENTITY, entity)
			.addOptional(NeoApoliContextParameters.ENTITY_POS, entity.map(Entity::getPos)));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	default void validate(ErrorReporter reporter) {
		condition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.ENTITY))
			.makeChild(".condition"));
	}

	static <M extends TestEntityMetaCondition> MapCodec<M> codec(BiFunction<EntityCondition, EntityTarget, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntityCondition.CODEC.fieldOf("condition").forGetter(TestEntityMetaCondition::condition),
			EntityTarget.CODEC.fieldOf("entity").forGetter(TestEntityMetaCondition::entity)
		).apply(instance, constructor));
	}

	static <M extends TestEntityMetaCondition> PacketCodec<RegistryByteBuf, M> packetCodec(BiFunction<EntityCondition, EntityTarget, M> constructor) {
		return PacketCodec.tuple(
			EntityCondition.PACKET_CODEC, TestEntityMetaCondition::condition,
			EntityTarget.PACKET_CODEC, TestEntityMetaCondition::entity,
			constructor
		);
	}

}

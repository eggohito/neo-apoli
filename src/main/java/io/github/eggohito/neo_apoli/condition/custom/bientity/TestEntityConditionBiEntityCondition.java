package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record TestEntityConditionBiEntityCondition(EntityCondition entityCondition, EntityParameter entity) implements BiEntityCondition {

	public static final MapCodec<TestEntityConditionBiEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("entity_condition").forGetter(TestEntityConditionBiEntityCondition::entityCondition),
		EntityParameter.CODEC.fieldOf("entity").forGetter(TestEntityConditionBiEntityCondition::entity)
	).apply(instance, TestEntityConditionBiEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, TestEntityConditionBiEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, TestEntityConditionBiEntityCondition::entityCondition,
		EntityParameter.PACKET_CODEC, TestEntityConditionBiEntityCondition::entity,
		TestEntityConditionBiEntityCondition::new
	);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_ENTITY_CONDITION;
	}

	@Override
	public boolean test(Context context) {

		Context entityConditionContext = context
			.copy(builder -> builder.add(ContextParameters.THIS_ENTITY, context.required(this.entity().getParameter())))
			.makeChild("entity_condition");

		return entityCondition().test(entityConditionContext);

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(this.entity().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		BiEntityCondition.super.validate(reporter);
		entityCondition().validate(reporter.makeChild("entity_condition"));
	}

}

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
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class TestEntityConditionBiEntityCondition extends BiEntityCondition {

	public static final MapCodec<TestEntityConditionBiEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("entity_condition").forGetter(TestEntityConditionBiEntityCondition::entityCondition),
		EntityParameter.CODEC.fieldOf("entity").forGetter(TestEntityConditionBiEntityCondition::entity)
	).apply(instance, TestEntityConditionBiEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, TestEntityConditionBiEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, TestEntityConditionBiEntityCondition::entityCondition,
		EntityParameter.PACKET_CODEC, TestEntityConditionBiEntityCondition::entity,
		TestEntityConditionBiEntityCondition::new
	);

	private final EntityCondition entityCondition;
	private final EntityParameter entity;

	public TestEntityConditionBiEntityCondition(EntityCondition entityCondition, EntityParameter entity) {
		this.entityCondition = entityCondition;
		this.entity = entity;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_ENTITY_CONDITION;
	}

	@Override
	protected boolean impl(Context context) {

		Entity entity = context.required(this.entity().getParameter());
		Vec3d pos = entity.getPos();

		Context entityContext = context.copy(builder -> builder
			.withContextType(ContextTypes.merge(context.getType(), ContextTypes.ENTITY))
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, pos));

		return entityCondition().test(entityContext.makeChild(".entity_condition"));

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(this.entity().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		entityCondition().validate(reporter
			.withContextType(ContextTypes.merge(reporter.getContextType(), ContextTypes.ENTITY))
			.makeChild(".entity_condition"));
	}

}

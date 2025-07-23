package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.OffsetMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

@EqualsAndHashCode
@Data
public final class OffsetEntityCondition extends EntityCondition implements OffsetMetaCondition<EntityCondition> {

	public static final MapCodec<OffsetEntityCondition> CODEC = MapCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> OffsetMetaCondition.codec(EntityCondition.CODEC, OffsetEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, OffsetEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(OffsetEntityCondition.class.getSimpleName(), () -> OffsetMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, OffsetEntityCondition::new));

	private final EntityCondition condition;
	private final Vec3d offset;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.OFFSET;
	}

	@Override
	protected boolean impl(Context context) {

		Vec3d offsetPos = context.required(ContextParameters.ENTITY_POS).add(offset());
		Context conditionContext = context.copy(builder -> builder.add(ContextParameters.ENTITY_POS, offsetPos));

		return condition().test(conditionContext.makeChild(".condition"));

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		OffsetMetaCondition.super.validate(reporter);
	}

}

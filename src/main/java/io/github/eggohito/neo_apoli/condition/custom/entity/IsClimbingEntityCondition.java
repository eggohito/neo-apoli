package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsClimbingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsClimbingEntityCondition> CODEC = MapCodec.unit(IsClimbingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsClimbingEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(IsClimbingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_CLIMBING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(ContextParameters.THIS_ENTITY)
				.stream()
				.filter(LivingEntity.class::isInstance)
				.map(LivingEntity.class::cast)
				.filter(entity -> context.markActive(this))
				.anyMatch(LivingEntity::isClimbing);
		}

		finally {
			context.markInActive(this);
		}

	}

}

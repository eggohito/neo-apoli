package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record IsClimbingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsClimbingEntityCondition> CODEC = MapCodec.unit(IsClimbingEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsClimbingEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsClimbingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_CLIMBING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(NeoApoliContextKeys.THIS_ENTITY)
				.stream()
				.filter(LivingEntity.class::isInstance)
				.map(LivingEntity.class::cast)
				.filter(entity -> context.markActive(this))
				.anyMatch(LivingEntity::onClimbable);
		}

		finally {
			context.markInActive(this);
		}

	}

}

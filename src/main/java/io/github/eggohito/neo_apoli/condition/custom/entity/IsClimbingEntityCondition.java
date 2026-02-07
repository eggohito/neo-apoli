package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public enum IsClimbingEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<IsClimbingEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsClimbingEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_CLIMBING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
				.stream()
				.filter(LivingEntity.class::isInstance)
				.map(LivingEntity.class::cast)
				.filter(entity -> context.visitor().push(this))
				.anyMatch(LivingEntity::onClimbable);
		}

		finally {
			context.visitor().pop(this);
		}

	}

}

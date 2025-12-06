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

public record IsFallFlyingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsFallFlyingEntityCondition> CODEC = MapCodec.unit(IsFallFlyingEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsFallFlyingEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsFallFlyingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_FALL_FLYING;
	}

	@Override
	public boolean test(Context context) {
		return context.nullable(NeoApoliContextKeys.THIS_ENTITY) instanceof LivingEntity livingEntity
			&& livingEntity.isFallFlying();
	}

}

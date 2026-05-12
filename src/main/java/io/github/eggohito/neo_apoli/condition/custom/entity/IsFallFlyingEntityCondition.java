package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public enum IsFallFlyingEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<IsFallFlyingEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsFallFlyingEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_FALL_FLYING;
	}

	@Override
	public boolean test(Context context) {
		return context.getNullable(NeoApoliContextParams.THIS_ENTITY) instanceof LivingEntity livingEntity
			&& livingEntity.isFallFlying();
	}

}

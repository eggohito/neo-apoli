package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public enum IsInvisibleEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<IsInvisibleEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsInvisibleEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_INVISIBLE;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
				.stream()
				.filter(entity -> context.visitor().push(this))
				.anyMatch(Entity::isInvisible);
		}

		finally {
			context.visitor().pop(this);
		}

	}

}

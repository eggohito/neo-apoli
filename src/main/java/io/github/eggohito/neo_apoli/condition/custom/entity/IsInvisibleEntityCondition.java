package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsInvisibleEntityCondition() implements EntityCondition {

	public static final MapCodec<IsInvisibleEntityCondition> CODEC = MapCodec.unit(IsInvisibleEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsInvisibleEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsInvisibleEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_INVISIBLE;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(NeoApoliContextKeys.THIS_ENTITY)
				.stream()
				.filter(entity -> context.markActive(this))
				.anyMatch(Entity::isInvisible);
		}

		finally {
			context.markInActive(this);
		}

	}

}

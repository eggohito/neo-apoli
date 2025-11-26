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

public record IsSprintingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsSprintingEntityCondition> CODEC = MapCodec.unit(IsSprintingEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsSprintingEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsSprintingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SPRINTING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(NeoApoliContextKeys.THIS_ENTITY)
				.stream()
				.filter(entity -> context.markActive(this))
				.anyMatch(Entity::isSprinting);
		}

		finally {
			context.markInActive(this);
		}

	}

}

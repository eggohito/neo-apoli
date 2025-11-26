package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HasCollidedHorizontallyEntityCondition() implements EntityCondition {

	public static final MapCodec<HasCollidedHorizontallyEntityCondition> CODEC = MapCodec.unit(HasCollidedHorizontallyEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, HasCollidedHorizontallyEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(HasCollidedHorizontallyEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_COLLIDED_HORIZONTALLY;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.map(entity -> entity.horizontalCollision)
			.orElse(false);
	}

}

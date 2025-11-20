package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record HasCollidedHorizontallyEntityCondition() implements EntityCondition {

	public static final MapCodec<HasCollidedHorizontallyEntityCondition> CODEC = MapCodec.unit(HasCollidedHorizontallyEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, HasCollidedHorizontallyEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(HasCollidedHorizontallyEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_COLLIDED_HORIZONTALLY;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextParameters.THIS_ENTITY)
			.map(entity -> entity.horizontalCollision)
			.orElse(false);
	}

}

package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsInvisibleEntityCondition() implements EntityCondition {

	public static final MapCodec<IsInvisibleEntityCondition> CODEC = MapCodec.unit(IsInvisibleEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsInvisibleEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(IsInvisibleEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_INVISIBLE;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(ContextParameters.THIS_ENTITY)
				.stream()
				.filter(entity -> context.markActive(this))
				.anyMatch(Entity::isInvisible);
		}

		finally {
			context.markInActive(this);
		}

	}

}

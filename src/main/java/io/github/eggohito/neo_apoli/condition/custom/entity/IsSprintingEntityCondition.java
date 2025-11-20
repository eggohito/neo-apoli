package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsSprintingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsSprintingEntityCondition> CODEC = MapCodec.unit(IsSprintingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsSprintingEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(IsSprintingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SPRINTING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.optional(NeoApoliContextParameters.THIS_ENTITY)
				.stream()
				.filter(entity -> context.markActive(this))
				.anyMatch(Entity::isSprinting);
		}

		finally {
			context.markInActive(this);
		}

	}

}

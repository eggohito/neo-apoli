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

public record IsSneakingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsSneakingEntityCondition> CODEC = MapCodec.unit(IsSneakingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsSneakingEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(IsSneakingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SNEAKING;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(ContextParameters.THIS_ENTITY)
			.stream()
			.anyMatch(Entity::isSneaking);
	}

}

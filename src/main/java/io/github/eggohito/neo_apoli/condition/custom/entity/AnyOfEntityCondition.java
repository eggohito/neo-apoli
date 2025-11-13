package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AnyOfMetaCondition<EntityCondition> {

	public static final MapCodec<AnyOfEntityCondition> CODEC = MapCodecUtil.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(EntityCondition.CODEC, AnyOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AnyOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedEntityCondition(EntityCondition condition) implements EntityCondition, InvertedMetaCondition<EntityCondition> {

	public static final MapCodec<InvertedEntityCondition> CODEC = MapCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(EntityCondition.CODEC, InvertedEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, InvertedEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AllOfMetaCondition<EntityCondition> {

	public static final MapCodec<AllOfEntityCondition> CODEC = MapCodecUtil.lazy(AllOfEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(EntityCondition.CODEC, AllOfEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(EntityCondition.PACKET_CODEC, AllOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

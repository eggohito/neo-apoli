package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantEntityCondition(boolean value) implements EntityCondition, ConstantMetaCondition {

	public static final Codec<ConstantEntityCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantEntityCondition::new);

	public static final MapCodec<ConstantEntityCondition> CODEC = ConstantMetaCondition.codec(ConstantEntityCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantEntityCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

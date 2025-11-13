package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantKeyCondition(boolean value) implements KeyCondition, ConstantMetaCondition {

	public static final Codec<ConstantKeyCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantKeyCondition::new);

	public static final MapCodec<ConstantKeyCondition> CODEC = ConstantMetaCondition.codec(ConstantKeyCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantKeyCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}

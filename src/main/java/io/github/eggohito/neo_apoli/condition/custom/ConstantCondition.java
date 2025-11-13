package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantCondition(boolean value) implements ConstantMetaCondition {

	public static final Codec<ConstantCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantCondition::new);

	public static final MapCodec<ConstantCondition> CODEC = ConstantMetaCondition.codec(ConstantCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.CONSTANT;
	}

}

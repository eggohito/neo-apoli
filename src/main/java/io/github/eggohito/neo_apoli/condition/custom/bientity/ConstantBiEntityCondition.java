package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantBiEntityCondition(boolean value) implements BiEntityCondition, ConstantMetaCondition {

	public static final Codec<ConstantBiEntityCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantBiEntityCondition::new);

	public static final MapCodec<ConstantBiEntityCondition> CODEC = ConstantMetaCondition.codec(ConstantBiEntityCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantBiEntityCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition, InvertedMetaCondition<BiEntityCondition> {

	public static final MapCodec<InvertedBiEntityCondition> CODEC = MapCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BiEntityCondition.CODEC, InvertedBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBiEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, InvertedBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}

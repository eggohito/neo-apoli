package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition, InvertedMetaCondition<BiEntityCondition, BiEntityConditionType<?>> {

	public static final MapCodec<InvertedBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BiEntityCondition.CODEC, InvertedBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, InvertedBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.INVERTED;
	}

}

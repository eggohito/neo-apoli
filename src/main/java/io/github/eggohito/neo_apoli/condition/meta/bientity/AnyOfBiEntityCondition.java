package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfBiEntityCondition(List<BiEntityCondition> conditions) implements BiEntityCondition, AnyOfMetaCondition<BiEntityCondition, BiEntityConditionType<?>> {

	public static final MapCodec<AnyOfBiEntityCondition> CODEC = NeoApoliCodecs.lazyMap(AnyOfBiEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(BiEntityCondition.CODEC, AnyOfBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AnyOfBiEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, AnyOfBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ANY_OF;
	}

}

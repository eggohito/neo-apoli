package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfBiEntityCondition(List<BiEntityCondition> conditions) implements BiEntityCondition, AllOfMetaCondition<BiEntityCondition, BiEntityConditionType<?>> {

	public static final MapCodec<AllOfBiEntityCondition> CODEC = NeoApoliMapCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(BiEntityCondition.CODEC, AllOfBiEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfBiEntityCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AllOfBiEntityCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(BiEntityCondition.PACKET_CODEC, AllOfBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.ALL_OF;
	}

}

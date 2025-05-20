package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record CompareBiEntityCondition(Comparison comparison) implements BiEntityCondition, CompareMetaCondition<BiEntityConditionType<?>> {

	public static final MapCodec<CompareBiEntityCondition> CODEC = CompareMetaCondition.codec(CompareBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareBiEntityCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE;
	}

}

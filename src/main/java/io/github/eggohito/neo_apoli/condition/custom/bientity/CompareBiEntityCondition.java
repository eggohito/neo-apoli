package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record CompareBiEntityCondition(Comparison comparison) implements BiEntityCondition, CompareMetaCondition {

	public static final MapCodec<CompareBiEntityCondition> CODEC = CompareMetaCondition.codec(CompareBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareBiEntityCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}

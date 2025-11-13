package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record CompareToRangeBiEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BiEntityCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBiEntityCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeBiEntityCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}

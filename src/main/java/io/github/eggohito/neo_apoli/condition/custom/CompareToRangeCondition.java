package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record CompareToRangeCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.COMPARE_TO_RANGE;
	}

}

package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record CompareToRangeEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements EntityCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEntityCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeEntityCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

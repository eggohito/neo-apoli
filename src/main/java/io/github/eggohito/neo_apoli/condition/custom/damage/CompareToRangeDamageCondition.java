package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record CompareToRangeDamageCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements DamageCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeDamageCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeDamageCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeDamageCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}

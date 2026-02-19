package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeDamageCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements DamageCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeDamageCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeDamageCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.COMPARE_TO_RANGE;
	}

}

package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliDamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeDamageCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements DamageCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeDamageCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeDamageCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeDamageCondition::new);

	@Override
	public DamageCondition.Type<?> getType() {
		return NeoApoliDamageConditionTypes.COMPARE_TO_RANGE;
	}

}

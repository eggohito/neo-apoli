package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeItemCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements ItemCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeItemCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeItemCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeItemCondition::new);

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.COMPARE_TO_RANGE;
	}

}

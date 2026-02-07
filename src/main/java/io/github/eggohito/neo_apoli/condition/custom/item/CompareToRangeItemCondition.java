package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeItemCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements ItemCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeItemCondition> MAP_CODEC = ICompareToRangeMetaCondition.mapCodec(CompareToRangeItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeItemCondition> STREAM_CODEC = ICompareToRangeMetaCondition.streamCodec(CompareToRangeItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.COMPARE_TO_RANGE;
	}

}

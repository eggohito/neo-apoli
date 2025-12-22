package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeMetaCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeMetaCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeMetaCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeMetaCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeMetaCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.COMPARE_TO_RANGE;
	}

}

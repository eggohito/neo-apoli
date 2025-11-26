package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareToRangeCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeCondition> CODEC = CompareToRangeMetaCondition.createCodec(CompareToRangeCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeCondition> STREAM_CODEC = CompareToRangeMetaCondition.createStreamCodec(CompareToRangeCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.COMPARE_TO_RANGE;
	}

}

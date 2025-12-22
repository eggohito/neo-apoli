package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements EntityCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEntityCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeEntityCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

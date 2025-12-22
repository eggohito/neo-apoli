package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeBlockCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BlockCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBlockCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeBlockCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeWorldCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements WorldCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeWorldCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeWorldCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}

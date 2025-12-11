package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionType;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeWorldCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements WorldCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeWorldCondition> CODEC = CompareToRangeMetaCondition.createCodec(CompareToRangeWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeWorldCondition> STREAM_CODEC = CompareToRangeMetaCondition.createStreamCodec(CompareToRangeWorldCondition::new);

	@Override
	public WorldConditionType<?> getType() {
		return WorldConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return WorldCondition.super.asDisplayString();
	}

}

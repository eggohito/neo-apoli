package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeKeyCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements KeyCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeKeyCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeKeyCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.COMPARE_TO_RANGE;
	}

}

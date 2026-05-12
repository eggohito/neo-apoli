package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeBlockCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BlockCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBlockCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeBlockCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeBlockCondition::new);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.COMPARE_TO_RANGE;
	}

}

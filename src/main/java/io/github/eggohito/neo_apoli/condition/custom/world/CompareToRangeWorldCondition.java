package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeWorldCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements WorldCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeWorldCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeWorldCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeWorldCondition::new);

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.COMPARE_TO_RANGE;
	}

}

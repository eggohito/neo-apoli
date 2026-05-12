package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareCondition(Comparison comparison) implements CompareMetaCondition {

	public static final MapCodec<CompareCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareCondition::new);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.COMPARE;
	}

}

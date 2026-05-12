package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareBiEntityCondition(Comparison comparison) implements BiEntityCondition, CompareMetaCondition {

	public static final MapCodec<CompareBiEntityCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareBiEntityCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareBiEntityCondition::new);

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.COMPARE;
	}

}

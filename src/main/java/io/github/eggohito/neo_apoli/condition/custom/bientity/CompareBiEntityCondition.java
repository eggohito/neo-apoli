package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareBiEntityCondition(Comparison comparison) implements BiEntityCondition, ICompareMetaCondition {

	public static final MapCodec<CompareBiEntityCondition> MAP_CODEC = ICompareMetaCondition.mapCodec(CompareBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareBiEntityCondition> STREAM_CODEC = ICompareMetaCondition.streamCodec(CompareBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE;
	}

}

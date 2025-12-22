package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareMetaCondition(Comparison comparison) implements ICompareMetaCondition {

	public static final MapCodec<CompareMetaCondition> CODEC = ICompareMetaCondition.createCodec(CompareMetaCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareMetaCondition> STREAM_CODEC = ICompareMetaCondition.createStreamCodec(CompareMetaCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.COMPARE;
	}

}

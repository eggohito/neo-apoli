package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareKeyCondition(Comparison comparison) implements KeyCondition, ICompareMetaCondition {

	public static final MapCodec<CompareKeyCondition> MAP_CODEC = ICompareMetaCondition.mapCodec(CompareKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareKeyCondition> STREAM_CODEC = ICompareMetaCondition.streamCodec(CompareKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.COMPARE;
	}

}

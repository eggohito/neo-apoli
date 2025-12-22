package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareBlockCondition(Comparison comparison) implements BlockCondition, ICompareMetaCondition {

	public static final MapCodec<CompareBlockCondition> CODEC = ICompareMetaCondition.createCodec(CompareBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareBlockCondition> STREAM_CODEC = ICompareMetaCondition.createStreamCodec(CompareBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}

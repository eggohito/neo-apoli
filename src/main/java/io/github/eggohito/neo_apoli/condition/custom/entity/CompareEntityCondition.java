package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareEntityCondition(Comparison comparison) implements EntityCondition, ICompareMetaCondition {

	public static final MapCodec<CompareEntityCondition> CODEC = ICompareMetaCondition.createCodec(CompareEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareEntityCondition> STREAM_CODEC = ICompareMetaCondition.createStreamCodec(CompareEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}

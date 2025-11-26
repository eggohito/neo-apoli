package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareItemCondition(Comparison comparison) implements ItemCondition, CompareMetaCondition {

	public static final MapCodec<CompareItemCondition> CODEC = CompareMetaCondition.createCodec(CompareItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareItemCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareItemCondition(Comparison comparison) implements ItemCondition, CompareMetaCondition {

	public static final MapCodec<CompareItemCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareItemCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareItemCondition::new);

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.COMPARE;
	}

}

package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicItemCondition(BooleanProvider value) implements ItemCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicItemCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicItemCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicItemCondition::new);

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.DYNAMIC;
	}

}

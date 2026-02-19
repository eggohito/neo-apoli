package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicItemCondition(BooleanProvider value) implements ItemCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicItemCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicItemCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.DYNAMIC;
	}

}

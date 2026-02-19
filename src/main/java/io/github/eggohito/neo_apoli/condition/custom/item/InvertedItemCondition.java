package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedItemCondition(ItemCondition condition) implements ItemCondition, InvertedMetaCondition<ItemCondition> {

	public static final MapCodec<InvertedItemCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(ItemCondition.CODEC, InvertedItemCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedItemCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(ItemCondition.STREAM_CODEC, InvertedItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.INVERTED;
	}

}

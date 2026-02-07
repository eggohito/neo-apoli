package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, IAnyOfMetaCondition<ItemCondition> {

	public static final MapCodec<AnyOfItemCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(ItemCondition.CODEC, AnyOfItemCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfItemCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(ItemCondition.STREAM_CODEC, AnyOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ANY_OF;
	}

}

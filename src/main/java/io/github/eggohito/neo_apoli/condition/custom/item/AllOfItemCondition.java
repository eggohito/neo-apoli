package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, IAllOfMetaCondition<ItemCondition> {

	public static final MapCodec<AllOfItemCondition> CODEC = MapCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createCodec(ItemCondition.CODEC, AllOfItemCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfItemCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> IAllOfMetaCondition.createStreamCodec(ItemCondition.STREAM_CODEC, AllOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}

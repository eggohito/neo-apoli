package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantItemCondition(boolean value) implements ItemCondition, ConstantMetaCondition {

	public static final Codec<ConstantItemCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantItemCondition::new);

	public static final MapCodec<ConstantItemCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantItemCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantItemCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.CONSTANT;
	}

}

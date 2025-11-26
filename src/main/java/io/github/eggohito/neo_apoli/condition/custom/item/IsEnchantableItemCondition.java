package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record IsEnchantableItemCondition() implements ItemCondition {

	public static final MapCodec<IsEnchantableItemCondition> CODEC = MapCodec.unit(IsEnchantableItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsEnchantableItemCondition> STREAM_CODEC = StreamCodecUtil.unit(IsEnchantableItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_ENCHANTABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.stream()
			.anyMatch(ItemStack::isEnchantable);
	}

}

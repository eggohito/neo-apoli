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

public record IsDamageableItemCondition() implements ItemCondition {

	public static final MapCodec<IsDamageableItemCondition> CODEC = MapCodec.unit(IsDamageableItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsDamageableItemCondition> STREAM_CODEC = StreamCodecUtil.unit(IsDamageableItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_DAMAGEABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.stream()
			.anyMatch(ItemStack::isDamageableItem);
	}

}

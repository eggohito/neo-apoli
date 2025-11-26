package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record IsFoodItemCondition() implements ItemCondition {

	public static final MapCodec<IsFoodItemCondition> CODEC = MapCodec.unit(IsFoodItemCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsFoodItemCondition> STREAM_CODEC = StreamCodecUtil.unit(IsFoodItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_FOOD;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.stream()
			.anyMatch(stack -> stack.has(DataComponents.FOOD));
	}

}

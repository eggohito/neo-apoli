package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public enum IsDamageableItemCondition implements ItemCondition {

	INSTANCE;

	public static final MapCodec<IsDamageableItemCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsDamageableItemCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.IS_DAMAGEABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.ITEM_STACK)
			.stream()
			.anyMatch(ItemStack::isDamageableItem);
	}

}

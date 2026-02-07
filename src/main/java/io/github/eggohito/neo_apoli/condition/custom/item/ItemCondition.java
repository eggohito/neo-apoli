package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface ItemCondition extends Condition {

	Codec<ItemCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(ItemConditionType.CODEC.dispatch(ItemCondition::getType, ItemConditionType::mapCodec), ConstantItemCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, ItemCondition> STREAM_CODEC = ItemConditionType.STREAM_CODEC.dispatch(ItemCondition::getType, ItemConditionType::streamCodec);

	@Override
	ItemConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ITEM_STACK);
	}

}

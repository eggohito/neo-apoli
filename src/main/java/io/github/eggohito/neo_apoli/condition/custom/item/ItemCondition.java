package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface ItemCondition extends Condition {

	Codec<ItemCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(ItemConditionType.CODEC.dispatch(ItemCondition::getType, ItemConditionType::mapCodec), ConstantItemCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, ItemCondition> PACKET_CODEC = ItemConditionType.PACKET_CODEC.dispatch(ItemCondition::getType, ItemConditionType::packetCodec);

	@Override
	ItemConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ITEM_STACK);
	}

	@Override
	default String asDisplayString() {
		return "Item condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}

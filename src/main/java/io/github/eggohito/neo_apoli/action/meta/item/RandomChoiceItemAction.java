package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record RandomChoiceItemAction(WeightedList<ItemAction> actions) implements ItemAction, RandomChoiceMetaAction<ItemAction, ItemActionType<?>> {

	public static final MapCodec<RandomChoiceItemAction> CODEC = NeoApoliMapCodecs.lazy(RandomChoiceItemAction.class.getSimpleName(), () -> RandomChoiceMetaAction.codec(ItemAction.CODEC, RandomChoiceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChoiceItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChoiceItemAction.class.getSimpleName(), () -> RandomChoiceMetaAction.packetCodec(ItemAction.PACKET_CODEC, RandomChoiceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHOICE;
	}

}

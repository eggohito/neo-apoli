package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceItemAction(List<ItemAction> actions) implements ItemAction, SequenceMetaAction<ItemAction, ItemActionType<?>> {

	public static final MapCodec<SequenceItemAction> CODEC = NeoApoliMapCodecs.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.codec(ItemAction.CODEC, SequenceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(ItemAction.PACKET_CODEC, SequenceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.SEQUENCE;
	}

}

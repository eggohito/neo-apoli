package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceItemAction(ItemAction successAction, Optional<ItemAction> failAction, float chance) implements ItemAction, RandomChanceMetaAction<ItemAction, ItemActionType<?>> {

	public static final MapCodec<RandomChanceItemAction> CODEC = NeoApoliMapCodecs.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(ItemAction.CODEC, RandomChanceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(ItemAction.PACKET_CODEC, RandomChanceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHANCE;
	}

}

package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceItemAction(ItemAction successAction, Optional<ItemAction> failAction, NumberProvider chance) implements ItemAction, RandomChanceMetaAction<ItemAction> {

	public static final MapCodec<RandomChanceItemAction> CODEC = MapCodecUtil.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(ItemAction.CODEC, RandomChanceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceItemAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(ItemAction.PACKET_CODEC, RandomChanceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void execute(Context context) {
		RandomChanceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		this.execute(context);
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}

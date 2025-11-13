package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingItemAction() implements ItemAction, NothingMetaAction {

	public static final Codec<NothingItemAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingItemAction::new);

	public static final MapCodec<NothingItemAction> CODEC = MapCodec.unit(NothingItemAction::new);

	public static final PacketCodec<RegistryByteBuf, NothingItemAction> PACKET_CODEC = PacketCodecUtil.unit(NothingItemAction::new);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.NOTHING;
	}

	@Override
	public void execute(Context context) {

	}

	@Override
	public void serverExecute(ServerContext context) {

	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}

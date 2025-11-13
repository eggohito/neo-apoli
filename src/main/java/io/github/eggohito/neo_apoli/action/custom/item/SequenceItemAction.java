package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceItemAction(List<ItemAction> actions) implements ItemAction, SequenceMetaAction<ItemAction> {

	public static final MapCodec<SequenceItemAction> CODEC = MapCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.codec(ItemAction.CODEC, SequenceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceItemAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(ItemAction.PACKET_CODEC, SequenceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.SEQUENCE;
	}

	@Override
	public void execute(Context context) {
		SequenceMetaAction.super.execute(context);
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

package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceItemAction(Identifier value) implements ItemAction, ReferenceMetaAction<ItemAction> {

	public static final MapCodec<ReferenceItemAction> CODEC = ReferenceMetaAction.codec(ReferenceItemAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceItemAction::new);

	@Override
	public Pair<Class<ItemAction>, String> classAndName() {
		return Pair.of(ItemAction.class, "Bi-entity action");
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.REFERENCE;
	}

	@Override
	public void execute(Context context) {
		ReferenceMetaAction.super.execute(context);
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

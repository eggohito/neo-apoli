package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class NothingItemAction extends ItemAction implements NothingMetaAction {

	public static final MapCodec<NothingItemAction> CODEC = NothingMetaAction.codec(NothingItemAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingItemAction> PACKET_CODEC = NothingMetaAction.packetCodec(NothingItemAction::new);

	public NothingItemAction() {

	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.NOTHING;
	}

	@Override
	protected void impl(ServerContext context) {

	}

	@Override
	public void validate(ErrorReporter reporter) {

	}

}

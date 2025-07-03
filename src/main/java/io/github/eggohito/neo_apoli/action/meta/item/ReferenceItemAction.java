package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class ReferenceItemAction extends ItemAction implements ReferenceMetaAction<ItemAction> {

	public static final MapCodec<ReferenceItemAction> CODEC = ReferenceMetaAction.codec(ReferenceItemAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceItemAction::new);

	private final Identifier value;

	public ReferenceItemAction(Identifier value) {
		this.value = value;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.REFERENCE;
	}

	@Override
	public void impl(ServerContext context) {
		ReferenceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaAction.super.validate(reporter);
	}

}

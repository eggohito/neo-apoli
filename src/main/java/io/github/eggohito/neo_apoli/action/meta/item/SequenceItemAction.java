package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class SequenceItemAction extends ItemAction implements SequenceMetaAction<ItemAction> {

	public static final MapCodec<SequenceItemAction> CODEC = MapCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.codec(ItemAction.CODEC, SequenceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceItemAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(ItemAction.PACKET_CODEC, SequenceItemAction::new));

	private final List<ItemAction> actions;

	public SequenceItemAction(List<ItemAction> actions) {
		this.actions = actions;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.SEQUENCE;
	}

	@Override
	protected void impl(ServerContext context) {
		SequenceMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		SequenceMetaAction.super.validate(reporter);
	}

}

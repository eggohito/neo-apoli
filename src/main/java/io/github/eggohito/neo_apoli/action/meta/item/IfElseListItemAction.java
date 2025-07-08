package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
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
public final class IfElseListItemAction extends ItemAction implements IfElseListMetaAction<ItemAction, ItemCondition> {

	public static final MapCodec<IfElseListItemAction> CODEC = MapCodecUtil.lazy(IfElseListItemAction.class.getSimpleName(), () -> IfElseListMetaAction.codec(ItemCondition.CODEC, ItemAction.CODEC, IfElseListItemAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListItemAction> PACKET_CODEC = PacketCodecUtil.lazy(IfElseListItemAction.class.getSimpleName(), () -> IfElseListMetaAction.packetCodec(ItemCondition.PACKET_CODEC, ItemAction.PACKET_CODEC, IfElseListItemAction::new));

	private final List<Entry<ItemCondition, ItemAction>> entries;

	public IfElseListItemAction(List<Entry<ItemCondition, ItemAction>> entries) {
		this.entries = entries;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.IF_ELSE_LIST;
	}

	@Override
	public void impl(ServerContext context) {
		IfElseListMetaAction.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseListMetaAction.super.validate(reporter);
	}

}

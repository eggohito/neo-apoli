package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Data
public final class IfElseItemAction extends ItemAction implements IfElseMetaAction<ItemAction, ItemCondition> {

	public static final MapCodec<IfElseItemAction> CODEC = NeoApoliMapCodecs.lazy(IfElseItemAction.class.getSimpleName(), () -> IfElseMetaAction.codec(ItemCondition.CODEC, ItemAction.CODEC, IfElseItemAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseItemAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(ItemCondition.PACKET_CODEC, ItemAction.PACKET_CODEC, IfElseItemAction::new));

	private final ItemCondition condition;
	private final ItemAction ifAction;

	private final Optional<ItemAction> elseAction;

	public IfElseItemAction(ItemCondition condition, ItemAction ifAction, Optional<ItemAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.IF_ELSE;
	}

	@Override
	public void impl(Context context) {
		IfElseMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		IfElseMetaAction.super.validate(reporter);
	}

}

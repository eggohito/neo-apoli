package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceItemAction(List<ItemAction> actions) implements ItemAction, SequenceMetaAction<ItemAction> {

	public static final MapCodec<SequenceItemAction> CODEC = MapCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.createCodec(ItemAction.CODEC, SequenceItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceItemAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.createStreamCodec(ItemAction.STREAM_CODEC, SequenceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.SEQUENCE;
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}

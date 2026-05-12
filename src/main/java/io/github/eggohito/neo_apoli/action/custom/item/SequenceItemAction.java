package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceItemAction(List<ItemAction> actions) implements ItemAction, SequenceMetaAction<ItemAction> {

	public static final MapCodec<SequenceItemAction> MAP_CODEC = MapCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.mapCodec(ItemAction.CODEC, SequenceItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceItemAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceItemAction.class.getSimpleName(), () -> SequenceMetaAction.streamCodec(ItemAction.STREAM_CODEC, SequenceItemAction::new));

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.SEQUENCE;
	}

}

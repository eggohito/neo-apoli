package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface ItemAction extends Action {

	Codec<ItemAction> CODEC = Codec.recursive(ItemAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ItemActionType.CODEC.dispatch(ItemAction::getType, ItemActionType::mapCodec), codec.listOf().xmap(SequenceItemAction::new, SequenceItemAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, ItemAction> STREAM_CODEC = ItemActionType.STREAM_CODEC.dispatch(ItemAction::getType, ItemActionType::streamCodec);

	@Override
	ItemActionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.SLOT_ACCESS);
	}

}

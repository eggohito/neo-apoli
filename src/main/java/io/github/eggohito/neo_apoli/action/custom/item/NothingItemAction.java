package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NothingItemAction() implements ItemAction, NothingMetaAction {

	public static final Codec<NothingItemAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingItemAction::new);

	public static final MapCodec<NothingItemAction> CODEC = MapCodec.unit(NothingItemAction::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingItemAction> STREAM_CODEC = StreamCodecUtil.unit(NothingItemAction::new);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.NOTHING;
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}

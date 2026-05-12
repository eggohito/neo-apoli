package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingItemAction implements ItemAction, NothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingItemAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingItemAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.NOTHING;
	}

}

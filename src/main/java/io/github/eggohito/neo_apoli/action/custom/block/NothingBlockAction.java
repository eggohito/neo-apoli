package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingBlockAction implements BlockAction, NothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingBlockAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingBlockAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.NOTHING;
	}

}

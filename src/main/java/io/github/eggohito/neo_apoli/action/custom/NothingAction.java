package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingAction implements NothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public ActionType<?> getType() {
		return ActionTypes.NOTHING;
	}

}

package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingMetaAction implements INothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingMetaAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingMetaAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.NOTHING;
	}

}

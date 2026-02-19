package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingBiEntityAction implements BiEntityAction, NothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingBiEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingBiEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.NOTHING;
	}

}

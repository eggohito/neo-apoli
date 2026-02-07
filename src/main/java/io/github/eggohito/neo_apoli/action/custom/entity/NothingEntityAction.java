package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.INothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingEntityAction implements EntityAction, INothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.NOTHING;
	}

}

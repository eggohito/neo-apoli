package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NothingEntityAction implements EntityAction, NothingMetaAction {

	INSTANCE;

	public static final MapCodec<NothingEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.NOTHING;
	}

}

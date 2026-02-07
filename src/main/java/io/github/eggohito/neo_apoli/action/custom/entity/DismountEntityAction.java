package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public enum DismountEntityAction implements EntityAction {

	INSTANCE;

	public static final MapCodec<DismountEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, DismountEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DISMOUNT;
	}

	@Override
	public void execute(Context context) {
		context.getOptional(NeoApoliContextParams.THIS_ENTITY).ifPresent(Entity::stopRiding);
	}

}

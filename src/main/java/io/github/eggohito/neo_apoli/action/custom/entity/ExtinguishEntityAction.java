package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public enum ExtinguishEntityAction implements EntityAction {

	INSTANCE;

	public static final MapCodec<ExtinguishEntityAction> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExtinguishEntityAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXTINGUISH;
	}

	@Override
	public void execute(Context context) {
		context.getOptional(NeoApoliContextParams.THIS_ENTITY).ifPresent(Entity::extinguishFire);
	}

}

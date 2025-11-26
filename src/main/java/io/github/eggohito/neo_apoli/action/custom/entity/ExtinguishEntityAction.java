package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ExtinguishEntityAction() implements EntityAction {

	public static final MapCodec<ExtinguishEntityAction> CODEC = MapCodec.unit(ExtinguishEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExtinguishEntityAction> STREAM_CODEC = StreamCodecUtil.unit(ExtinguishEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXTINGUISH;
	}

	@Override
	public void execute(Context context) {
		context.optional(NeoApoliContextKeys.THIS_ENTITY).ifPresent(Entity::extinguishFire);
	}

}

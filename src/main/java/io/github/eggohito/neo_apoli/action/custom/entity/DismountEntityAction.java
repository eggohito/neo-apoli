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

public record DismountEntityAction() implements EntityAction {

	public static final MapCodec<DismountEntityAction> CODEC = MapCodec.unit(DismountEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DismountEntityAction> STREAM_CODEC = StreamCodecUtil.unit(DismountEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DISMOUNT;
	}

	@Override
	public void execute(Context context) {
		context.optional(NeoApoliContextKeys.THIS_ENTITY).ifPresent(Entity::stopRiding);
	}

}

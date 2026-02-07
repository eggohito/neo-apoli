package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ILoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopEntityAction(Optional<EntityAction> beforeAction, Optional<EntityAction> afterAction, NumberProvider iterations, EntityAction action) implements EntityAction, ILoopMetaAction<EntityAction> {

	public static final MapCodec<LoopEntityAction> MAP_CODEC = MapCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> ILoopMetaAction.mapCodec(EntityAction.CODEC, LoopEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> ILoopMetaAction.streamCodec(EntityAction.STREAM_CODEC, LoopEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.LOOP;
	}

}

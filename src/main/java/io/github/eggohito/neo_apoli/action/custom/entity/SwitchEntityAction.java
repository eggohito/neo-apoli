package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SwitchMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchEntityAction(List<Case<EntityCondition, EntityAction>> cases, EntityAction defaultAction) implements EntityAction, SwitchMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<SwitchEntityAction> MAP_CODEC = MapCodecUtil.lazy(SwitchEntityAction.class.getSimpleName(), () -> SwitchMetaAction.mapCodec(EntityCondition.CODEC, EntityAction.CODEC, SwitchEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchEntityAction.class.getSimpleName(), () -> SwitchMetaAction.streamCodec(EntityCondition.STREAM_CODEC, EntityAction.STREAM_CODEC, SwitchEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SWITCH;
	}

}

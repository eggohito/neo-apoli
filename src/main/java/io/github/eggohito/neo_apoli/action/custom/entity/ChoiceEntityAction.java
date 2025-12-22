package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceEntityAction(List<Case<EntityCondition, EntityAction>> cases, EntityAction defaultAction) implements EntityAction, IChoiceMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<ChoiceEntityAction> CODEC = MapCodecUtil.lazy(ChoiceEntityAction.class.getSimpleName(), () -> IChoiceMetaAction.createCodec(EntityCondition.CODEC, EntityAction.CODEC, ChoiceEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceEntityAction.class.getSimpleName(), () -> IChoiceMetaAction.createStreamCodec(EntityCondition.STREAM_CODEC, EntityAction.STREAM_CODEC, ChoiceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.CHOICE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

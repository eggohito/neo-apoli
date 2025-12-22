package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalEntityAction(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) implements EntityAction, IConditionalMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<ConditionalEntityAction> CODEC = MapCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> IConditionalMetaAction.createCodec(EntityCondition.CODEC, EntityAction.CODEC, ConditionalEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> IConditionalMetaAction.createStreamCodec(EntityCondition.STREAM_CODEC, EntityAction.STREAM_CODEC, ConditionalEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.CONDITIONAL;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

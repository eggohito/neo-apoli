package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalEntityAction(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) implements EntityAction, ConditionalMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<ConditionalEntityAction> MAP_CODEC = MapCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> ConditionalMetaAction.mapCodec(EntityCondition.CODEC, EntityAction.CODEC, ConditionalEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> ConditionalMetaAction.streamCodec(EntityCondition.STREAM_CODEC, EntityAction.STREAM_CODEC, ConditionalEntityAction::new));

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.CONDITIONAL;
	}

}

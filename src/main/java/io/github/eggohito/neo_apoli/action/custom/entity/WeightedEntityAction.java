package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IWeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedEntityAction(ShufflingList<EntityAction> entries) implements EntityAction, IWeightedMetaAction<EntityAction> {

	public static final MapCodec<WeightedEntityAction> CODEC = MapCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> IWeightedMetaAction.createCodec(EntityAction.CODEC, WeightedEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> IWeightedMetaAction.createStreamCodec(EntityAction.STREAM_CODEC, WeightedEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.WEIGHTED;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

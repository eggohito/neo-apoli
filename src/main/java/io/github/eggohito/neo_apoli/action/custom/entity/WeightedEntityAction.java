package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;

public record WeightedEntityAction(WeightedList<EntityAction> entries) implements EntityAction, WeightedMetaAction<EntityAction> {

	public static final MapCodec<WeightedEntityAction> MAP_CODEC = MapCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> WeightedMetaAction.mapCodec(EntityAction.CODEC, WeightedEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> WeightedMetaAction.streamCodec(EntityAction.STREAM_CODEC, WeightedEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.WEIGHTED;
	}

}

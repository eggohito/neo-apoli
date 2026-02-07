package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ISequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceEntityAction(List<EntityAction> actions) implements EntityAction, ISequenceMetaAction<EntityAction> {

	public static final MapCodec<SequenceEntityAction> MAP_CODEC = MapCodecUtil.lazy(SequenceEntityAction.class.getSimpleName(), () -> ISequenceMetaAction.mapCodec(EntityAction.CODEC, SequenceEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceEntityAction.class.getSimpleName(), () -> ISequenceMetaAction.streamCodec(EntityAction.STREAM_CODEC, SequenceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SEQUENCE;
	}

}

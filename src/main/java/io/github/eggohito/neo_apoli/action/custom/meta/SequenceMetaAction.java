package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceMetaAction(List<Action> actions) implements ISequenceMetaAction<Action> {

	public static final MapCodec<SequenceMetaAction> CODEC = MapCodecUtil.lazy(SequenceMetaAction.class.getSimpleName(), () -> ISequenceMetaAction.createCodec(Action.CODEC, SequenceMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceMetaAction.class.getSimpleName(), () -> ISequenceMetaAction.createStreamCodec(Action.STREAM_CODEC, SequenceMetaAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.SEQUENCE;
	}

}

package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceAction(List<Action> actions) implements SequenceMetaAction<Action> {

	public static final MapCodec<SequenceAction> CODEC = MapCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.createCodec(Action.CODEC, SequenceAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.createStreamCodec(Action.STREAM_CODEC, SequenceAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.SEQUENCE;
	}

}

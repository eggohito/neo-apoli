package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceAction(List<Action> actions) implements SequenceMetaAction<Action> {

	public static final MapCodec<SequenceAction> MAP_CODEC = MapCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.mapCodec(Action.CODEC, SequenceAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.streamCodec(Action.STREAM_CODEC, SequenceAction::new));

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.SEQUENCE;
	}

}

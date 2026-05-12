package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopAction(Optional<Action> beforeAction, Optional<Action> afterAction, NumberProvider iterations, Action action) implements LoopMetaAction<Action> {

	public static final MapCodec<LoopAction> MAP_CODEC = MapCodecUtil.lazy(LoopAction.class.getSimpleName(), () -> LoopMetaAction.mapCodec(Action.CODEC, LoopAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopAction.class.getSimpleName(), () -> LoopMetaAction.streamCodec(Action.STREAM_CODEC, LoopAction::new));

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.LOOP;
	}

}

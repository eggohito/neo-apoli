package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopMetaAction(Optional<Action> beforeAction, Optional<Action> afterAction, NumberProvider iterations, Action action) implements ILoopMetaAction<Action> {

	public static final MapCodec<LoopMetaAction> CODEC = MapCodecUtil.lazy(LoopMetaAction.class.getSimpleName(), () -> ILoopMetaAction.createCodec(Action.CODEC, LoopMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopMetaAction.class.getSimpleName(), () -> ILoopMetaAction.createStreamCodec(Action.STREAM_CODEC, LoopMetaAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.LOOP;
	}

}

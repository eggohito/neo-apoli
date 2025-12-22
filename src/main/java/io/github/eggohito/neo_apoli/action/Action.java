package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Action extends ContextAware, StringDisplayable {

	Codec<Action> CODEC = Codec.recursive(Action.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ActionType.CODEC.dispatch(Action::getType, ActionType::mapCodec), codec.listOf().xmap(SequenceMetaAction::new, SequenceMetaAction::actions), NothingMetaAction.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, Action> STREAM_CODEC = ActionType.STREAM_CODEC.dispatch(Action::getType, ActionType::streamCodec);

	ActionType<?> getType();

	void execute(Context context);

}

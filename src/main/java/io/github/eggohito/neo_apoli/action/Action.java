package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.custom.SequenceAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Action extends ContextUser {

	Codec<Action> CODEC = Codec.recursive(Action.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ActionType.CODEC.dispatch(Action::getType, ActionType::mapCodec), codec.listOf().xmap(SequenceAction::new, SequenceAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, Action> STREAM_CODEC = ActionType.STREAM_CODEC.dispatch(Action::getType, ActionType::streamCodec);

	ActionType<?> getType();

	void execute(Context context);

}

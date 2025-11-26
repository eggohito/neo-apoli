package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalAction(Condition condition, Action ifAction, Optional<Action> elseAction) implements ConditionalMetaAction<Condition, Action> {

	public static final MapCodec<ConditionalAction> CODEC = MapCodecUtil.lazy(ConditionalAction.class.getSimpleName(), () -> ConditionalMetaAction.createCodec(Condition.CODEC, Action.CODEC, ConditionalAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalAction.class.getSimpleName(), () -> ConditionalMetaAction.createStreamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, ConditionalAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.CONDITIONAL;
	}

}

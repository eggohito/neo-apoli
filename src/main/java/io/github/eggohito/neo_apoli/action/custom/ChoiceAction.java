package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceAction(List<Case<Condition, Action>> cases, Action defaultAction) implements ChoiceMetaAction<Condition, Action> {

	public static final MapCodec<ChoiceAction> CODEC = MapCodecUtil.lazy(ChoiceAction.class.getSimpleName(), () -> ChoiceMetaAction.createCodec(Condition.CODEC, Action.CODEC, ChoiceAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceAction.class.getSimpleName(), () -> ChoiceMetaAction.createStreamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, ChoiceAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.CHOICE;
	}

}

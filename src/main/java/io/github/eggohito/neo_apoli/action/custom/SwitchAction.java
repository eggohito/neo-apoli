package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.SwitchMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchAction(List<Case<Condition, Action>> cases, Action defaultAction) implements SwitchMetaAction<Condition, Action> {

	public static final MapCodec<SwitchAction> MAP_CODEC = MapCodecUtil.lazy(SwitchAction.class.getSimpleName(), () -> SwitchMetaAction.mapCodec(Condition.CODEC, Action.CODEC, SwitchAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchAction.class.getSimpleName(), () -> SwitchMetaAction.streamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, SwitchAction::new));

	@Override
	public ActionType<?> getType() {
		return ActionTypes.SWITCH;
	}

}

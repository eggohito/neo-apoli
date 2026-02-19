package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchMetaAction(List<Case<Condition, Action>> cases, Action defaultAction) implements ISwitchMetaAction<Condition, Action> {

	public static final MapCodec<SwitchMetaAction> MAP_CODEC = MapCodecUtil.lazy(SwitchMetaAction.class.getSimpleName(), () -> ISwitchMetaAction.mapCodec(Condition.CODEC, Action.CODEC, SwitchMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchMetaAction.class.getSimpleName(), () -> ISwitchMetaAction.streamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, SwitchMetaAction::new));

	@Override
	public MetaActionType<?> getType() {
		return MetaActionTypes.SWITCH;
	}

}

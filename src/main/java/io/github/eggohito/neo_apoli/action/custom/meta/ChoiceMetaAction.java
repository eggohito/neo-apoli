package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceMetaAction(List<Case<Condition, Action>> cases, Action defaultAction) implements IChoiceMetaAction<Condition, Action> {

	public static final MapCodec<ChoiceMetaAction> MAP_CODEC = MapCodecUtil.lazy(ChoiceMetaAction.class.getSimpleName(), () -> IChoiceMetaAction.mapCodec(Condition.CODEC, Action.CODEC, ChoiceMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceMetaAction.class.getSimpleName(), () -> IChoiceMetaAction.streamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, ChoiceMetaAction::new));

	@Override
	public MetaActionType<?> getType() {
		return MetaActionTypes.CHOICE;
	}

}

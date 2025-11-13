package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceAction(List<Case<Condition, Action>> cases, Action defaultAction) implements ChoiceMetaAction<Condition, Action> {

	public static final MapCodec<ChoiceAction> CODEC = MapCodecUtil.lazy(ChoiceAction.class.getSimpleName(), () -> ChoiceMetaAction.codec(Condition.BASE_CODEC, Action.BASE_CODEC, ChoiceAction::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceAction> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceAction.class.getSimpleName(), () -> ChoiceMetaAction.packetCodec(Condition.BASE_PACKET_CODEC, Action.BASE_PACKET_CODEC, ChoiceAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.CHOICE;
	}

}

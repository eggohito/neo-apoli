package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record ConditionalAction(Condition condition, Action ifAction, Optional<Action> elseAction) implements ConditionalMetaAction<Condition, Action> {

	public static final MapCodec<ConditionalAction> CODEC = MapCodecUtil.lazy(ConditionalAction.class.getSimpleName(), () -> ConditionalMetaAction.codec(Condition.BASE_CODEC, Action.BASE_CODEC, ConditionalAction::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalAction> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalAction.class.getSimpleName(), () -> ConditionalMetaAction.packetCodec(Condition.BASE_PACKET_CODEC, Action.BASE_PACKET_CODEC, ConditionalAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.CONDITIONAL;
	}

}

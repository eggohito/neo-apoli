package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceAction(List<Action> actions) implements SequenceMetaAction<Action> {

	public static final MapCodec<SequenceAction> CODEC = MapCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.codec(Action.BASE_CODEC, SequenceAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(Action.BASE_PACKET_CODEC, SequenceAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.SEQUENCE;
	}

}

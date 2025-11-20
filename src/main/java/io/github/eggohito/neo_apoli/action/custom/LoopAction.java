package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record LoopAction(Optional<Action> beforeAction, Optional<Action> afterAction, NumberProvider iterations, Action action) implements LoopMetaAction<Action> {

	public static final MapCodec<LoopAction> CODEC = MapCodecUtil.lazy(LoopAction.class.getSimpleName(), () -> LoopMetaAction.codec(Action.CODEC, LoopAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(Action.PACKET_CODEC, LoopAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.LOOP;
	}

}

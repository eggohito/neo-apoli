package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record WeightedAction(WeightedList<Action> entries) implements WeightedMetaAction<Action> {

	public static final MapCodec<WeightedAction> CODEC = MapCodecUtil.lazy(WeightedAction.class.getSimpleName(), () -> WeightedMetaAction.codec(Action.BASE_CODEC, WeightedAction::new));
	public static final PacketCodec<RegistryByteBuf, WeightedAction> PACKET_CODEC = PacketCodecUtil.lazy(WeightedAction.class.getSimpleName(), () -> WeightedMetaAction.packetCodec(Action.BASE_PACKET_CODEC, WeightedAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.WEIGHTED;
	}

}

package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record WeightedBiEntityAction(WeightedList<BiEntityAction> entries) implements BiEntityAction, WeightedMetaAction<BiEntityAction> {

	public static final MapCodec<WeightedBiEntityAction> CODEC = MapCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> WeightedMetaAction.codec(BiEntityAction.CODEC, WeightedBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, WeightedBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> WeightedMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, WeightedBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.WEIGHTED;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

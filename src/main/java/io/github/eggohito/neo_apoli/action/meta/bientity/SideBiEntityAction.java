package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SideBiEntityAction(BiEntityAction action, Side side) implements BiEntityAction, SideMetaAction<BiEntityAction, BiEntityActionType<?>> {

	public static final MapCodec<SideBiEntityAction> CODEC = NeoApoliCodecs.lazyMap(SideBiEntityAction.class.getSimpleName(), () -> SideMetaAction.codec(BiEntityAction.CODEC, SideBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SideBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SideBiEntityAction.class.getSimpleName(), () -> SideMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, SideBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SIDE;
	}

}

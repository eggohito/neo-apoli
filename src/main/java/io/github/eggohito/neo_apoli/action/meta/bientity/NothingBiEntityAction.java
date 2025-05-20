package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingBiEntityAction() implements BiEntityAction, NothingMetaAction<BiEntityActionType<?>> {

	public static final MapCodec<NothingBiEntityAction> CODEC = MapCodec.unit(NothingBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingBiEntityAction> PACKET_CODEC = PacketCodec.unit(new NothingBiEntityAction());

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.NOTHING;
	}

}

package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingBiEntityAction() implements BiEntityAction, NothingMetaAction {

	public static final Codec<NothingBiEntityAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingBiEntityAction::new);

	public static final MapCodec<NothingBiEntityAction> CODEC = MapCodec.unit(NothingBiEntityAction::new);

	public static final PacketCodec<RegistryByteBuf, NothingBiEntityAction> PACKET_CODEC = PacketCodecUtil.unit(NothingBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.NOTHING;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

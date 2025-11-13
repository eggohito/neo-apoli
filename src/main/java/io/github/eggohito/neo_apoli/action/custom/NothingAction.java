package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingAction() implements NothingMetaAction {

	public static final Codec<NothingAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingAction::new);

	public static final MapCodec<NothingAction> CODEC = MapCodec.unit(NothingAction::new);

	public static final PacketCodec<RegistryByteBuf, NothingAction> PACKET_CODEC = PacketCodecUtil.unit(NothingAction::new);

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.NOTHING;
	}

}

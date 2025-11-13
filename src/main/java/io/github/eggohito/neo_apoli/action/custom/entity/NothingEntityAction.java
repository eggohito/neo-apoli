package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingEntityAction() implements EntityAction, NothingMetaAction {

	public static final Codec<NothingEntityAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingEntityAction::new);

	public static final MapCodec<NothingEntityAction> CODEC = MapCodec.unit(NothingEntityAction::new);

	public static final PacketCodec<RegistryByteBuf, NothingEntityAction> PACKET_CODEC = PacketCodecUtil.unit(NothingEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.NOTHING;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

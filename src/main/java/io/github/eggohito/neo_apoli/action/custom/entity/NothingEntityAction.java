package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.INothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NothingEntityAction() implements EntityAction, INothingMetaAction {

	public static final Codec<NothingEntityAction> INLINE_CODEC = INothingMetaAction.createEmptyInputCodec(NothingEntityAction::new);

	public static final MapCodec<NothingEntityAction> CODEC = MapCodec.unit(NothingEntityAction::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingEntityAction> STREAM_CODEC = StreamCodecUtil.unit(NothingEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.NOTHING;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

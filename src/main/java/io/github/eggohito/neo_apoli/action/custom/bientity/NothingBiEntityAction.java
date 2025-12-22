package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.INothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NothingBiEntityAction() implements BiEntityAction, INothingMetaAction {

	public static final Codec<NothingBiEntityAction> INLINE_CODEC = INothingMetaAction.createEmptyInputCodec(NothingBiEntityAction::new);

	public static final MapCodec<NothingBiEntityAction> CODEC = MapCodec.unit(NothingBiEntityAction::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingBiEntityAction> STREAM_CODEC = StreamCodecUtil.unit(NothingBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.NOTHING;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

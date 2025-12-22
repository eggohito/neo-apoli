package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NothingMetaAction() implements INothingMetaAction {

	public static final Codec<NothingMetaAction> INLINE_CODEC = INothingMetaAction.createEmptyInputCodec(NothingMetaAction::new);

	public static final MapCodec<NothingMetaAction> CODEC = MapCodec.unit(NothingMetaAction::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingMetaAction> STREAM_CODEC = StreamCodecUtil.unit(NothingMetaAction::new);

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.NOTHING;
	}

}

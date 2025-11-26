package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NothingBlockAction() implements BlockAction, NothingMetaAction {

	public static final Codec<NothingBlockAction> INLINE_CODEC = NothingMetaAction.createEmptyInputCodec(NothingBlockAction::new);

	public static final MapCodec<NothingBlockAction> CODEC = MapCodec.unit(NothingBlockAction::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, NothingBlockAction> STREAM_CODEC = StreamCodecUtil.unit(NothingBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.NOTHING;
	}

	@Override
	public void execute(Context context) {
		NothingMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		NothingMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}

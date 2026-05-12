package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceBlockAction(List<BlockAction> actions) implements BlockAction, SequenceMetaAction<BlockAction> {

	public static final MapCodec<SequenceBlockAction> MAP_CODEC = MapCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.mapCodec(BlockAction.CODEC, SequenceBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.streamCodec(BlockAction.STREAM_CODEC, SequenceBlockAction::new));

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.SEQUENCE;
	}

}

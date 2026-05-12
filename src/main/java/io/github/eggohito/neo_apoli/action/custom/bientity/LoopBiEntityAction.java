package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopBiEntityAction(Optional<BiEntityAction> beforeAction, Optional<BiEntityAction> afterAction, NumberProvider iterations, BiEntityAction action) implements BiEntityAction, LoopMetaAction<BiEntityAction> {

	public static final MapCodec<LoopBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.mapCodec(BiEntityAction.CODEC, LoopBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopBiEntityAction.class.getSimpleName(), () -> LoopMetaAction.streamCodec(BiEntityAction.STREAM_CODEC, LoopBiEntityAction::new));

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.LOOP;
	}

}

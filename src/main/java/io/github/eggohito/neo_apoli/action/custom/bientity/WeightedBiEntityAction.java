package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;

public record WeightedBiEntityAction(WeightedList<BiEntityAction> entries) implements BiEntityAction, WeightedMetaAction<BiEntityAction> {

	public static final MapCodec<WeightedBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> WeightedMetaAction.mapCodec(BiEntityAction.CODEC, WeightedBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> WeightedMetaAction.streamCodec(BiEntityAction.STREAM_CODEC, WeightedBiEntityAction::new));

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.WEIGHTED;
	}

}

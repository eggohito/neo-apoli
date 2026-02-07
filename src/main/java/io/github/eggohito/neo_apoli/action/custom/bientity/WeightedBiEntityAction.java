package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IWeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedBiEntityAction(ShufflingList<BiEntityAction> entries) implements BiEntityAction, IWeightedMetaAction<BiEntityAction> {

	public static final MapCodec<WeightedBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> IWeightedMetaAction.mapCodec(BiEntityAction.CODEC, WeightedBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedBiEntityAction.class.getSimpleName(), () -> IWeightedMetaAction.streamCodec(BiEntityAction.STREAM_CODEC, WeightedBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.WEIGHTED;
	}

}

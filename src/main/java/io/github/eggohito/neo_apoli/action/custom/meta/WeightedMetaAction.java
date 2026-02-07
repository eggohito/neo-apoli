package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedMetaAction(ShufflingList<Action> entries) implements IWeightedMetaAction<Action> {

	public static final MapCodec<WeightedMetaAction> MAP_CODEC = MapCodecUtil.lazy(WeightedMetaAction.class.getSimpleName(), () -> IWeightedMetaAction.mapCodec(Action.CODEC, WeightedMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedMetaAction.class.getSimpleName(), () -> IWeightedMetaAction.streamCodec(Action.STREAM_CODEC, WeightedMetaAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.WEIGHTED;
	}

}

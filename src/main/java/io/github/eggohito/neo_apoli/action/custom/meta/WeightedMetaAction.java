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

	public static final MapCodec<WeightedMetaAction> CODEC = MapCodecUtil.lazy(WeightedMetaAction.class.getSimpleName(), () -> IWeightedMetaAction.createCodec(Action.CODEC, WeightedMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedMetaAction.class.getSimpleName(), () -> IWeightedMetaAction.createStreamCodec(Action.STREAM_CODEC, WeightedMetaAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.WEIGHTED;
	}

}

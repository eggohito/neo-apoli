package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedAction(ShufflingList<Action> entries) implements WeightedMetaAction<Action> {

	public static final MapCodec<WeightedAction> CODEC = MapCodecUtil.lazy(WeightedAction.class.getSimpleName(), () -> WeightedMetaAction.createCodec(Action.CODEC, WeightedAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedAction.class.getSimpleName(), () -> WeightedMetaAction.createStreamCodec(Action.STREAM_CODEC, WeightedAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.WEIGHTED;
	}

}

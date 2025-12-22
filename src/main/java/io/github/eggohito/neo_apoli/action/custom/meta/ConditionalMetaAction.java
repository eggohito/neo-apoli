package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalMetaAction(Condition condition, Action ifAction, Optional<Action> elseAction) implements IConditionalMetaAction<Condition, Action> {

	public static final MapCodec<ConditionalMetaAction> CODEC = MapCodecUtil.lazy(ConditionalMetaAction.class.getSimpleName(), () -> IConditionalMetaAction.createCodec(Condition.CODEC, Action.CODEC, ConditionalMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalMetaAction.class.getSimpleName(), () -> IConditionalMetaAction.createStreamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC, ConditionalMetaAction::new));

	@Override
	public MetaActionType<?> getType() {
		return MetaActionTypes.CONDITIONAL;
	}

}

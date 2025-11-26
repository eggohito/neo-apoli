package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceBiEntityAction(List<Case<BiEntityCondition, BiEntityAction>> cases, BiEntityAction defaultAction) implements BiEntityAction, ChoiceMetaAction<BiEntityCondition, BiEntityAction> {

	public static final MapCodec<ChoiceBiEntityAction> CODEC = MapCodecUtil.lazy(ChoiceBiEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.createCodec(BiEntityCondition.CODEC, BiEntityAction.CODEC, ChoiceBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceBiEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.createStreamCodec(BiEntityCondition.STREAM_CODEC, BiEntityAction.STREAM_CODEC, ChoiceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.CHOICE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalBiEntityAction(BiEntityCondition condition, BiEntityAction ifAction, Optional<BiEntityAction> elseAction) implements BiEntityAction, IConditionalMetaAction<BiEntityCondition, BiEntityAction> {

	public static final MapCodec<ConditionalBiEntityAction> CODEC = MapCodecUtil.lazy(ConditionalBiEntityAction.class.getSimpleName(), () -> IConditionalMetaAction.createCodec(BiEntityCondition.CODEC, BiEntityAction.CODEC, ConditionalBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBiEntityAction.class.getSimpleName(), () -> IConditionalMetaAction.createStreamCodec(BiEntityCondition.STREAM_CODEC, BiEntityAction.STREAM_CODEC, ConditionalBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.CONDITIONAL;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ISwitchMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchBiEntityAction(List<Case<BiEntityCondition, BiEntityAction>> cases, BiEntityAction defaultAction) implements BiEntityAction, ISwitchMetaAction<BiEntityCondition, BiEntityAction> {

	public static final MapCodec<SwitchBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(SwitchBiEntityAction.class.getSimpleName(), () -> ISwitchMetaAction.mapCodec(BiEntityCondition.CODEC, BiEntityAction.CODEC, SwitchBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBiEntityAction.class.getSimpleName(), () -> ISwitchMetaAction.streamCodec(BiEntityCondition.STREAM_CODEC, BiEntityAction.STREAM_CODEC, SwitchBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SWITCH;
	}

}

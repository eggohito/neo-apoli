package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SwitchMetaAction;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchBiEntityAction(List<Case<BiEntityCondition, BiEntityAction>> cases, BiEntityAction defaultAction) implements BiEntityAction, SwitchMetaAction<BiEntityCondition, BiEntityAction> {

	public static final MapCodec<SwitchBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(SwitchBiEntityAction.class.getSimpleName(), () -> SwitchMetaAction.mapCodec(BiEntityCondition.CODEC, BiEntityAction.CODEC, SwitchBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBiEntityAction.class.getSimpleName(), () -> SwitchMetaAction.streamCodec(BiEntityCondition.STREAM_CODEC, BiEntityAction.STREAM_CODEC, SwitchBiEntityAction::new));

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.SWITCH;
	}

}

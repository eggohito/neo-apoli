package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceBiEntityAction(List<Case<BiEntityCondition, BiEntityAction>> cases, BiEntityAction defaultAction) implements BiEntityAction, ChoiceMetaAction<BiEntityCondition, BiEntityAction> {

	public static final MapCodec<ChoiceBiEntityAction> CODEC = MapCodecUtil.lazy(ChoiceBiEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.codec(BiEntityCondition.CODEC, BiEntityAction.CODEC, ChoiceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceBiEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.packetCodec(BiEntityCondition.PACKET_CODEC, BiEntityAction.PACKET_CODEC, ChoiceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.CHOICE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

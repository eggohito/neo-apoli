package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record IfElseBiEntityAction(BiEntityCondition condition, BiEntityAction ifAction, Optional<BiEntityAction> elseAction) implements BiEntityAction, IfElseMetaAction<BiEntityAction, BiEntityCondition, BiEntityActionType<?>, BiEntityConditionType<?>> {

	public static final MapCodec<IfElseBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(IfElseBiEntityAction.class.getSimpleName(), () -> IfElseMetaAction.codec(BiEntityCondition.CODEC, BiEntityAction.CODEC, IfElseBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseBiEntityAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(BiEntityCondition.PACKET_CODEC, BiEntityAction.PACKET_CODEC, IfElseBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.IF_ELSE;
	}

}

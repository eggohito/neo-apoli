package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceBiEntityAction(BiEntityAction successAction, Optional<BiEntityAction> failAction, float chance) implements BiEntityAction, RandomChanceMetaAction<BiEntityAction, BiEntityActionType<?>> {

	public static final MapCodec<RandomChanceBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(BiEntityAction.CODEC, RandomChanceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, RandomChanceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHANCE;
	}

}

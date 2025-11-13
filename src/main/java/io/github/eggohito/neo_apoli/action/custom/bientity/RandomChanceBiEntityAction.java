package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceBiEntityAction(BiEntityAction successAction, Optional<BiEntityAction> failAction, NumberProvider chance) implements BiEntityAction, RandomChanceMetaAction<BiEntityAction> {

	public static final MapCodec<RandomChanceBiEntityAction> CODEC = MapCodecUtil.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(BiEntityAction.CODEC, RandomChanceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, RandomChanceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

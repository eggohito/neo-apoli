package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, NumberProvider chance) implements EntityAction, RandomChanceMetaAction<EntityAction> {

	public static final MapCodec<RandomChanceEntityAction> CODEC = MapCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.codec(EntityAction.CODEC, RandomChanceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.packetCodec(EntityAction.PACKET_CODEC, RandomChanceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, float chance) implements EntityAction, RandomChanceMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<RandomChanceEntityAction> CODEC = NeoApoliCodecs.lazyMap("RandomChanceEntityAction", () -> RandomChanceMetaAction.codec(EntityAction.CODEC, RandomChanceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, RandomChanceEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("RandomChanceEntityAction", () -> RandomChanceMetaAction.packetCodec(EntityAction.PACKET_CODEC, RandomChanceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHANCE;
	}

}

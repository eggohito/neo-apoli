package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, float chance) implements EntityAction, RandomChanceMetaAction<EntityActionContext, EntityAction, EntityActionType<?>> {

	public static final MapCodec<RandomChanceEntityAction> CODEC = RandomChanceMetaAction.createCodec(EntityAction.CODEC, RandomChanceEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, RandomChanceEntityAction> PACKET_CODEC = RandomChanceMetaAction.createPacketCodec(EntityAction.PACKET_CODEC, RandomChanceEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHANCE;
	}

}

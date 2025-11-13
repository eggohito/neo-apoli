package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record ConditionalEntityAction(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) implements EntityAction, ConditionalMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<ConditionalEntityAction> CODEC = MapCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> ConditionalMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, ConditionalEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalEntityAction.class.getSimpleName(), () -> ConditionalMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, ConditionalEntityAction::new));
	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.CONDITIONAL;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

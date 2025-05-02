package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.OffsetMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public record OffsetEntityAction(EntityAction action, Vec3d offset) implements EntityAction, OffsetMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<OffsetEntityAction> CODEC = OffsetMetaAction.createCodec(EntityAction.CODEC, OffsetEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, OffsetEntityAction> PACKET_CODEC = OffsetMetaAction.createPacketCodec(EntityAction.PACKET_CODEC, OffsetEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.OFFSET;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return EntityAction.super.getAllowedParameters();
	}

}

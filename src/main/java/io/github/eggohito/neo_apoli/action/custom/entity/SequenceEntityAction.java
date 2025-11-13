package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceEntityAction(List<EntityAction> actions) implements EntityAction, SequenceMetaAction<EntityAction> {

	public static final MapCodec<SequenceEntityAction> CODEC = MapCodecUtil.lazy(SequenceEntityAction.class.getSimpleName(), () -> SequenceMetaAction.codec(EntityAction.CODEC, SequenceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceEntityAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(EntityAction.PACKET_CODEC, SequenceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SEQUENCE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceEntityAction(List<EntityAction> actions) implements EntityAction, SequenceMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<SequenceEntityAction> CODEC = NeoApoliCodecs.lazyMap("SequenceEntityAction", () -> SequenceMetaAction.createCodec(EntityAction.CODEC, SequenceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("SequenceEntityAction", () -> SequenceMetaAction.createPacketCodec(EntityAction.PACKET_CODEC, SequenceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SEQUENCE;
	}

}

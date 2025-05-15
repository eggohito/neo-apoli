package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record IfElseListEntityAction(List<Entry<EntityCondition, EntityAction>> entries) implements EntityAction, IfElseListMetaAction<EntityAction, EntityCondition, EntityActionType<?>, io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType<?>> {

	public static final MapCodec<IfElseListEntityAction> CODEC = NeoApoliCodecs.lazyMap("IfElseListEntityAction", () -> IfElseListMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, IfElseListEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("IfElseListEntityAction", () -> IfElseListMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, IfElseListEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.IF_ELSE_LIST;
	}

}

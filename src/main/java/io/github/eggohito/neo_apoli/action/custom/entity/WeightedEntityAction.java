package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record WeightedEntityAction(WeightedList<EntityAction> entries) implements EntityAction, WeightedMetaAction<EntityAction> {

	public static final MapCodec<WeightedEntityAction> CODEC = MapCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> WeightedMetaAction.codec(EntityAction.CODEC, WeightedEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, WeightedEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(WeightedEntityAction.class.getSimpleName(), () -> WeightedMetaAction.packetCodec(EntityAction.PACKET_CODEC, WeightedEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.WEIGHTED;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

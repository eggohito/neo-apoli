package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record LoopEntityAction(Optional<EntityAction> beforeAction, Optional<EntityAction> afterAction, NumberProvider iterations, EntityAction action) implements EntityAction, LoopMetaAction<EntityAction> {

	public static final MapCodec<LoopEntityAction> CODEC = MapCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> LoopMetaAction.codec(EntityAction.CODEC, LoopEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopEntityAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(EntityAction.PACKET_CODEC, LoopEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.LOOP;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

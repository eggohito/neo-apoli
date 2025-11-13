package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceEntityAction(Identifier value) implements EntityAction, ReferenceMetaAction<EntityAction> {

	public static final MapCodec<ReferenceEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceEntityAction::new);

	@Override
	public Pair<Class<EntityAction>, String> classAndName() {
		return Pair.of(EntityAction.class, "Entity action");
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

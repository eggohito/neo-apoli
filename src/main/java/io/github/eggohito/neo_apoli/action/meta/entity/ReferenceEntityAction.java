package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceEntityAction(Identifier value) implements EntityAction, ReferenceMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<ReferenceEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceEntityAction::new);

	@Override
	public ActionCategory<EntityAction> getCategory() {
		return EntityAction.super.getCategory();
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.REFERENCE;
	}

}

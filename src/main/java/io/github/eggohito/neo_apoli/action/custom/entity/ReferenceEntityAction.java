package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.EntityActionKind;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEntityAction(ResourceLocation value) implements EntityAction, ReferenceMetaAction<EntityAction> {

	public static final MapCodec<ReferenceEntityAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEntityAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceEntityAction::new);

	@Override
	public ActionKind<EntityAction> targetKind() {
		return EntityActionKind.INSTANCE;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.REFERENCE;
	}

}

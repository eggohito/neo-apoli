package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEntityAction(ResourceLocation value) implements EntityAction, ReferenceMetaAction<EntityAction> {

	public static final MapCodec<ReferenceEntityAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEntityAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceEntityAction::new);

	@Override
	public Action.Kind<EntityAction> targetKind() {
		return EntityAction.Kind.INSTANCE;
	}

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.REFERENCE;
	}

}

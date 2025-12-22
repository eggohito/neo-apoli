package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceEntityAction(ResourceLocation value) implements EntityAction, IReferenceMetaAction<EntityAction> {

	public static final MapCodec<ReferenceEntityAction> CODEC = IReferenceMetaAction.createCodec(ReferenceEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceEntityAction> STREAM_CODEC = IReferenceMetaAction.createStreamCodec(ReferenceEntityAction::new);

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

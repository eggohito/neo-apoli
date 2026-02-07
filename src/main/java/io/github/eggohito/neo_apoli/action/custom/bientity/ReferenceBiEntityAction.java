package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityAction(ResourceLocation value) implements BiEntityAction, IReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> MAP_CODEC = IReferenceMetaAction.mapCodec(ReferenceBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityAction> STREAM_CODEC = IReferenceMetaAction.streamCodec(ReferenceBiEntityAction::new);

	@Override
	public Pair<Class<BiEntityAction>, String> classAndName() {
		return Pair.of(BiEntityAction.class, "Bi-entity action");
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.REFERENCE;
	}

}

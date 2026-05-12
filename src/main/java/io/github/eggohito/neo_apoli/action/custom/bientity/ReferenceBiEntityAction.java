package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityAction(ResourceLocation value) implements BiEntityAction, ReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceBiEntityAction::new);

	@Override
	public Action.Kind<BiEntityAction> targetKind() {
		return BiEntityAction.Kind.INSTANCE;
	}

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.REFERENCE;
	}

}

package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.BiEntityActionKind;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityAction(ResourceLocation value) implements BiEntityAction, ReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceBiEntityAction::new);

	@Override
	public ActionKind<BiEntityAction> targetCategory() {
		return BiEntityActionKind.INSTANCE;
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.REFERENCE;
	}

}

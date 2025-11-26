package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBiEntityAction(ResourceLocation value) implements BiEntityAction, ReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> CODEC = ReferenceMetaAction.createCodec(ReferenceBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBiEntityAction> STREAM_CODEC = ReferenceMetaAction.createStreamCodec(ReferenceBiEntityAction::new);

	@Override
	public Pair<Class<BiEntityAction>, String> classAndName() {
		return Pair.of(BiEntityAction.class, "Bi-entity action");
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

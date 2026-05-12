package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceAction(ResourceLocation value) implements ReferenceMetaAction<Action> {

	public static final MapCodec<ReferenceAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceAction::new);

	@Override
	public Kind<Action> targetKind() {
		return Kind.INSTANCE;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.REFERENCE;
	}

}

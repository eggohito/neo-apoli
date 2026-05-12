package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBlockAction(ResourceLocation value) implements BlockAction, ReferenceMetaAction<BlockAction> {

	public static final MapCodec<ReferenceBlockAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceBlockAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBlockAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceBlockAction::new);

	@Override
	public Action.Kind<BlockAction> targetKind() {
		return BlockAction.Kind.INSTANCE;
	}

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.REFERENCE;
	}

}

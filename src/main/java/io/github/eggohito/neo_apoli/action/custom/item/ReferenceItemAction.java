package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceItemAction(ResourceLocation value) implements ItemAction, ReferenceMetaAction<ItemAction> {

	public static final MapCodec<ReferenceItemAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceItemAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceItemAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceItemAction::new);

	@Override
	public Action.Kind<ItemAction> targetKind() {
		return ItemAction.Kind.INSTANCE;
	}

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.REFERENCE;
	}

}

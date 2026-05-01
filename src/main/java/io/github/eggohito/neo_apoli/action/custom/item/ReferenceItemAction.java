package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.action.kind.custom.ItemActionKind;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceItemAction(ResourceLocation value) implements ItemAction, ReferenceMetaAction<ItemAction> {

	public static final MapCodec<ReferenceItemAction> MAP_CODEC = ReferenceMetaAction.mapCodec(ReferenceItemAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceItemAction> STREAM_CODEC = ReferenceMetaAction.streamCodec(ReferenceItemAction::new);

	@Override
	public ActionKind<ItemAction> targetCategory() {
		return ItemActionKind.INSTANCE;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.REFERENCE;
	}

}

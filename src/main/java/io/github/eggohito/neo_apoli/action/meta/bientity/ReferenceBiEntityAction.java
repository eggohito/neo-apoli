package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBiEntityAction(Identifier value) implements BiEntityAction, ReferenceMetaAction<BiEntityAction, BiEntityActionType<?>> {

	public static final MapCodec<ReferenceBiEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBiEntityAction::new);

	@Override
	public ActionCategory<BiEntityAction> getCategory() {
		return BiEntityAction.super.getCategory();
	}

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.REFERENCE;
	}

}

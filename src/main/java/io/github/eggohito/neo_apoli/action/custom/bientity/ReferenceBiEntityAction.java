package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBiEntityAction(Identifier value) implements BiEntityAction, ReferenceMetaAction<BiEntityAction> {

	public static final MapCodec<ReferenceBiEntityAction> CODEC = ReferenceMetaAction.codec(ReferenceBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBiEntityAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBiEntityAction::new);

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

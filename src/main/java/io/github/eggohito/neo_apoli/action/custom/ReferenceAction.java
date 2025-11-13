package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceAction(Identifier value) implements ReferenceMetaAction<Action> {

	public static final MapCodec<ReferenceAction> CODEC = ReferenceMetaAction.codec(ReferenceAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceAction::new);

	@Override
	public Pair<Class<Action>, String> classAndName() {
		return Pair.of(Action.class, "Action");
	}

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.REFERENCE;
	}

}

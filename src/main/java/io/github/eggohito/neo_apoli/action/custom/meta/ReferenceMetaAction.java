package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceMetaAction(ResourceLocation value) implements IReferenceMetaAction<Action> {

	public static final MapCodec<ReferenceMetaAction> MAP_CODEC = IReferenceMetaAction.mapCodec(ReferenceMetaAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceMetaAction> STREAM_CODEC = IReferenceMetaAction.streamCodec(ReferenceMetaAction::new);

	@Override
	public Pair<Class<Action>, String> classAndName() {
		return Pair.of(Action.class, "Action");
	}

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.REFERENCE;
	}

}

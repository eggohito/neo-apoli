package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceAction(ResourceLocation value) implements ReferenceMetaAction<Action> {

	public static final MapCodec<ReferenceAction> CODEC = ReferenceMetaAction.createCodec(ReferenceAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceAction> STREAM_CODEC = ReferenceMetaAction.createStreamCodec(ReferenceAction::new);

	@Override
	public Pair<Class<Action>, String> classAndName() {
		return Pair.of(Action.class, "Action");
	}

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.REFERENCE;
	}

}

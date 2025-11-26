package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceAction(Action successAction, Optional<Action> failAction, NumberProvider chance) implements RandomChanceMetaAction<Action> {

	public static final MapCodec<RandomChanceAction> CODEC = MapCodecUtil.lazy(RandomChanceAction.class.getSimpleName(), () -> RandomChanceMetaAction.createCodec(Action.CODEC, RandomChanceAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceAction.class.getSimpleName(), () -> RandomChanceMetaAction.createStreamCodec(Action.STREAM_CODEC, RandomChanceAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.RANDOM;
	}

}

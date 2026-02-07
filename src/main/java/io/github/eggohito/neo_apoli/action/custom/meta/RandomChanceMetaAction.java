package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceMetaAction(Action successAction, Optional<Action> failAction, NumberProvider chance) implements IRandomChanceMetaAction<Action> {

	public static final MapCodec<RandomChanceMetaAction> MAP_CODEC = MapCodecUtil.lazy(RandomChanceMetaAction.class.getSimpleName(), () -> IRandomChanceMetaAction.mapCodec(Action.CODEC, RandomChanceMetaAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceMetaAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceMetaAction.class.getSimpleName(), () -> IRandomChanceMetaAction.streamCodec(Action.STREAM_CODEC, RandomChanceMetaAction::new));

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.RANDOM;
	}

}

package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IRandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, NumberProvider chance) implements EntityAction, IRandomChanceMetaAction<EntityAction> {

	public static final MapCodec<RandomChanceEntityAction> CODEC = MapCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> IRandomChanceMetaAction.createCodec(EntityAction.CODEC, RandomChanceEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> IRandomChanceMetaAction.createStreamCodec(EntityAction.STREAM_CODEC, RandomChanceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}

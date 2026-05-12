package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceEntityAction(EntityAction successAction, Optional<EntityAction> failAction, NumberProvider chance) implements EntityAction, RandomChanceMetaAction<EntityAction> {

	public static final MapCodec<RandomChanceEntityAction> MAP_CODEC = MapCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.mapCodec(EntityAction.CODEC, RandomChanceEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceEntityAction.class.getSimpleName(), () -> RandomChanceMetaAction.streamCodec(EntityAction.STREAM_CODEC, RandomChanceEntityAction::new));

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.RANDOM_CHANCE;
	}

}

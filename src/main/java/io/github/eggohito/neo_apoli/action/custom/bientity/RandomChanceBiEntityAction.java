package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IRandomChanceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceBiEntityAction(BiEntityAction successAction, Optional<BiEntityAction> failAction, NumberProvider chance) implements BiEntityAction, IRandomChanceMetaAction<BiEntityAction> {

	public static final MapCodec<RandomChanceBiEntityAction> CODEC = MapCodecUtil.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> IRandomChanceMetaAction.createCodec(BiEntityAction.CODEC, RandomChanceBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceBiEntityAction.class.getSimpleName(), () -> IRandomChanceMetaAction.createStreamCodec(BiEntityAction.STREAM_CODEC, RandomChanceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.RANDOM_CHANCE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}

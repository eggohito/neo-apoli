package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record RandomChanceBlockAction(BlockAction successAction, Optional<BlockAction> failAction, NumberProvider chance) implements BlockAction, RandomChanceMetaAction<BlockAction> {

	public static final MapCodec<RandomChanceBlockAction> MAP_CODEC = MapCodecUtil.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.mapCodec(BlockAction.CODEC, RandomChanceBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceBlockAction.class.getSimpleName(), () -> RandomChanceMetaAction.streamCodec(BlockAction.STREAM_CODEC, RandomChanceBlockAction::new));

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.RANDOM_CHANCE;
	}

}

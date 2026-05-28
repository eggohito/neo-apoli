package io.github.eggohito.neo_apoli.provider.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBlockProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalBlockProvider(Condition condition, BlockProvider ifValue, BlockProvider elseValue) implements BlockProvider, ConditionalValueProvider<BlockProvider> {

	public static final MapCodec<ConditionalBlockProvider> CODEC = MapCodecUtil.lazy(ConditionalBlockProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(BlockProvider.CODEC, ConditionalBlockProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBlockProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBlockProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(BlockProvider.STREAM_CODEC, ConditionalBlockProvider::new));

	@Override
	public BlockProvider.Type<?> getType() {
		return NeoApoliBlockProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<CachedBlock> getBlock(Context context) {
		return this.nextOrElse(context, BlockProvider::getBlock, Optional::empty);
	}

}

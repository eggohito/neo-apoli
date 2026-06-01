package io.github.eggohito.neo_apoli.provider.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBlockProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SwitchBlockProvider(List<Case<Condition, BlockProvider>> cases, BlockProvider defaultValue) implements BlockProvider, SwitchValueProvider<BlockProvider> {

	public static final MapCodec<SwitchBlockProvider> CODEC = MapCodecUtil.lazy(SwitchBlockProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(BlockProvider.CODEC, SwitchBlockProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBlockProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBlockProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(BlockProvider.STREAM_CODEC, SwitchBlockProvider::new));

	@Override
	public BlockProvider.@NotNull Type<?> getType() {
		return NeoApoliBlockProviderTypes.SWITCH;
	}

	@Override
	public Optional<CachedBlock> getBlock(Context context) {
		return this.getOrDefault(context, BlockProvider::getBlock);
	}

}

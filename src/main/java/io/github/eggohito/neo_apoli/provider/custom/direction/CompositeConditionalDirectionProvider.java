package io.github.eggohito.neo_apoli.provider.custom.direction;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.CompositeConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliDirectionProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.conditional.CompositeConditional;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record CompositeConditionalDirectionProvider(List<CompositeConditional.Entry<DirectionProvider>> entries, DirectionProvider defaultValue) implements DirectionProvider, CompositeConditionalValueProvider<DirectionProvider> {

	public static final MapCodec<CompositeConditionalDirectionProvider> CODEC = MapCodecUtil.lazy(CompositeConditionalDirectionProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.mapCodec(DirectionProvider.CODEC, CompositeConditionalDirectionProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CompositeConditionalDirectionProvider> STREAM_CODEC = StreamCodecUtil.lazy(CompositeConditionalDirectionProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.streamCodec(DirectionProvider.STREAM_CODEC, CompositeConditionalDirectionProvider::new));

	@Override
	public DirectionProvider.@NotNull Type<?> getType() {
		return NeoApoliDirectionProviderTypes.COMPOSITE_CONDITIONAL;
	}

	@Override
	public Optional<Direction> getDirection(Context context) {
		return this.getOrDefault(context, DirectionProvider::getDirection);
	}

}

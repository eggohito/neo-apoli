package io.github.eggohito.neo_apoli.provider.custom.direction;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliDirectionProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SwitchDirectionProvider(List<Case<Condition, DirectionProvider>> cases, DirectionProvider defaultValue) implements DirectionProvider, SwitchValueProvider<DirectionProvider> {

	public static final MapCodec<SwitchDirectionProvider> CODEC = MapCodecUtil.lazy(SwitchDirectionProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(DirectionProvider.CODEC, SwitchDirectionProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchDirectionProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchDirectionProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(DirectionProvider.STREAM_CODEC, SwitchDirectionProvider::new));

	@Override
	public DirectionProvider.@NotNull Type<?> getType() {
		return NeoApoliDirectionProviderTypes.SWITCH;
	}

	@Override
	public Optional<Direction> getDirection(Context context) {
		return this.getOrDefault(context, DirectionProvider::getDirection);
	}

}

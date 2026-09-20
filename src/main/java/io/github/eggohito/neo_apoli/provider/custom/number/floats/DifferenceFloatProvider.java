package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.MultiFloatProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DifferenceFloatProvider(List<FloatProvider> values) implements MultiFloatProvider {

	public static final MapCodec<DifferenceFloatProvider> CODEC = MultiFloatProvider.mapCodec(DifferenceFloatProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DifferenceFloatProvider> STREAM_CODEC = MultiFloatProvider.streamCodec(DifferenceFloatProvider::new);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.DIFFERENCE;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		this.iterateAndProcess(context, (first, second) -> first - second, setter);
	}

}

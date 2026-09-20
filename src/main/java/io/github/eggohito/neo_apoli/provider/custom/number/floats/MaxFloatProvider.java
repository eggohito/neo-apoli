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

public record MaxFloatProvider(List<FloatProvider> values) implements MultiFloatProvider {

	public static final MapCodec<MaxFloatProvider> CODEC = MultiFloatProvider.mapCodec(MaxFloatProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MaxFloatProvider> STREAM_CODEC = MultiFloatProvider.streamCodec(MaxFloatProvider::new);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.MAX;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		this.iterateAndProcess(context, Math::max, setter);
	}

}

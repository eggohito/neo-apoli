package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SubtractNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<SubtractNumberProvider> MAP_CODEC = MultiNumberProvider.codec(SubtractNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, SubtractNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(SubtractNumberProvider::new);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.SUBTRACT;
	}

	@Override
	public double nextDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, (a, b) -> a - b, 0.0d);
	}

}

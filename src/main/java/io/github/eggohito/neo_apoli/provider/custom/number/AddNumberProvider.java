package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<AddNumberProvider> MAP_CODEC = MultiNumberProvider.codec(AddNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, AddNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(AddNumberProvider::new);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ADD;
	}

	@Override
	public double nextDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, Double::sum, 0.0D);
	}

	@Override
	public long nextLong(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextLong, Long::sum, 0L);
	}

}

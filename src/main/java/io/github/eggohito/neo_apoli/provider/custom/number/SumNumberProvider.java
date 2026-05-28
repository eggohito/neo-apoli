package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SumNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<SumNumberProvider> CODEC = MultiNumberProvider.codec(SumNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, SumNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(SumNumberProvider::new);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.SUM;
	}

	@Override
	public double getDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::getDouble, Double::sum, 0.0D);
	}

	@Override
	public long getLong(Context context) {
		return this.iterateAndProcess(context, NumberProvider::getLong, Long::sum, 0L);
	}

}

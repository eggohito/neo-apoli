package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MultiplyNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MultiplyNumberProvider> MAP_CODEC = MultiNumberProvider.codec(MultiplyNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(MultiplyNumberProvider::new);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.MULTIPLY;
	}

	@Override
	public double nextDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, (a, b) -> a * b, 0.0d);
	}

}

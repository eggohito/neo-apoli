package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ProductNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<ProductNumberProvider> CODEC = MultiNumberProvider.codec(ProductNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ProductNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(ProductNumberProvider::new);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.PRODUCT;
	}

	@Override
	public double getDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::getDouble, (a, b) -> a * b, 0.0d);
	}

}

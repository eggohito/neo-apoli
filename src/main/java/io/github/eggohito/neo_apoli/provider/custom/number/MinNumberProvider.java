package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MinNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MinNumberProvider> CODEC = MultiNumberProvider.codec(MinNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinNumberProvider> STREAM_CODEC = MultiNumberProvider.streamCodec(MinNumberProvider::new);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.MIN;
	}

	@Override
	public double getDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::getDouble, Math::min, 0.0d);
	}

}

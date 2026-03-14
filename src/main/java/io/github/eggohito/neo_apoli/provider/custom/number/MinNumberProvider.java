package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MinNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MinNumberProvider> MAP_CODEC = MultiNumberProvider.codec(MinNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinNumberProvider> STREAM_CODEC = MultiNumberProvider.packetCodec(MinNumberProvider::new);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.MIN;
	}

	@Override
	public double nextDouble(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, Math::min, 0.0d);
	}

}

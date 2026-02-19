package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<AddNumberProvider> MAP_CODEC = MultiNumberProvider.codec(AddNumberProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, AddNumberProvider> STREAM_CODEC = MultiNumberProvider.packetCodec(AddNumberProvider::new);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.ADD;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, Double::sum, 0.0d);
	}

}

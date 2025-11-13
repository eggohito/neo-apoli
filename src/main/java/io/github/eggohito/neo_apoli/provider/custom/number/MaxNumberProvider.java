package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MaxNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MaxNumberProvider> CODEC = MultiNumberProvider.codec(MaxNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, MaxNumberProvider> PACKET_CODEC = MultiNumberProvider.packetCodec(MaxNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MAX;
	}

	@Override
	public @NotNull Number next(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, Math::max, 0.0d);
	}

}

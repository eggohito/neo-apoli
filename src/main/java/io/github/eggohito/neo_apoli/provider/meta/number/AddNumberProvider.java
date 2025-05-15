package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.misc.MultiNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AddNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<AddNumberProvider> CODEC = MultiNumberProvider.simpleCodec(AddNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, AddNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(AddNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADD;
	}

	@Override
	public double doubleValue(Context context) {
		return this.iterateAndProcess(context, NumberProvider::doubleValue, Double::sum, 0.0D);
	}

}

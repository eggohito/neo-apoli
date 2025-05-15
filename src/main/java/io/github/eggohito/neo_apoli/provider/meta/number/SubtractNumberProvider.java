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

public record SubtractNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<SubtractNumberProvider> CODEC = MultiNumberProvider.simpleCodec(SubtractNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, SubtractNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(SubtractNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.SUBTRACT;
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, (a, b) -> a - b, 0.0D);
	}

}

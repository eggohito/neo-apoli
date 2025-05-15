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

public record MaxNumberProvider(List<NumberProvider> numbers) implements MultiNumberProvider {

	public static final MapCodec<MaxNumberProvider> CODEC = MultiNumberProvider.simpleCodec(MaxNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, MaxNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(MaxNumberProvider::new);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MAX;
	}

	@Override
	public double doubleValue(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, Math::max, 0.0D);
	}

	@Override
	public long longValue(Context context) {
		return iterateAndProcess(context, NumberProvider::longValue, Math::max, 0L);
	}

}

package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.misc.MultiNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class MaxNumberProvider extends NumberProvider implements MultiNumberProvider {

	public static final MapCodec<MaxNumberProvider> CODEC = MultiNumberProvider.simpleCodec(MaxNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, MaxNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(MaxNumberProvider::new);

	private final List<NumberProvider> numbers;

	public MaxNumberProvider(List<NumberProvider> numbers) {
		this.numbers = numbers;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MAX;
	}

	@Override
	protected Number impl(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, Math::max, 0.0D);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		MultiNumberProvider.super.validate(reporter);
	}

}

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

@EqualsAndHashCode
@Data
public final class MultiplyNumberProvider extends NumberProvider implements MultiNumberProvider {

	public static final MapCodec<MultiplyNumberProvider> CODEC = MultiNumberProvider.simpleCodec(MultiplyNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, MultiplyNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(MultiplyNumberProvider::new);

	private final List<NumberProvider> numbers;

	public MultiplyNumberProvider(List<NumberProvider> numbers) {
		this.numbers = numbers;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.MULTIPLY;
	}

	@Override
	protected Number impl(Context context) {
		return this.iterateAndProcess(context, NumberProvider::nextDouble, (a, b) -> a * b, 0.0D);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		MultiNumberProvider.super.validate(reporter);
	}

}

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
public final class SubtractNumberProvider extends NumberProvider implements MultiNumberProvider {

	public static final MapCodec<SubtractNumberProvider> CODEC = MultiNumberProvider.simpleCodec(SubtractNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, SubtractNumberProvider> PACKET_CODEC = MultiNumberProvider.simplePacketCodec(SubtractNumberProvider::new);

	private final List<NumberProvider> numbers;

	public SubtractNumberProvider(List<NumberProvider> numbers) {
		this.numbers = numbers;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.SUBTRACT;
	}

	@Override
	protected double doubleImpl(Context context) {
		return iterateAndProcess(context, NumberProvider::doubleValue, (a, b) -> a - b, 0.0D);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		MultiNumberProvider.super.validate(reporter);
	}

}

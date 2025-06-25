package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.random.Random;

@EqualsAndHashCode(callSuper = false)
@Data
public final class BinomialNumberProvider extends NumberProvider {

	public static final MapCodec<BinomialNumberProvider> CODEC = NeoApoliMapCodecs.lazy(BinomialNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("attempts").forGetter(BinomialNumberProvider::attempts),
		NumberProvider.CODEC.fieldOf("probability").forGetter(BinomialNumberProvider::probability)
	).apply(instance, BinomialNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, BinomialNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(BinomialNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::attempts,
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::probability,
		BinomialNumberProvider::new
	));

	private final NumberProvider attempts;
	private final NumberProvider probability;

	public BinomialNumberProvider(NumberProvider attempts, NumberProvider probability) {
		this.attempts = attempts;
		this.probability = probability;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BINOMIAL;
	}

	@Override
	protected double doubleImpl(Context context) {

		Random random = context.getWorld().getRandom();
		long result = 0;

		long attempts = attempts().longValue(context.makeChild(".attempts"));
		double probability = probability().doubleValue(context.makeChild(".probability"));

		for (int i = 0; !context.hasAnyErrors() && i < attempts; i++) {

			if (random.nextDouble() < probability) {
				result++;
			}

		}

		return result;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		attempts().validate(reporter.makeChild(".attempts"));
		probability().validate(reporter.makeChild(".probability"));

	}

}

package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.random.Random;

public record BinomialNumberProvider(NumberProvider attempts, NumberProvider probability) implements NumberProvider {

	public static final MapCodec<BinomialNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("attempts").forGetter(BinomialNumberProvider::attempts),
		NumberProvider.CODEC.fieldOf("probability").forGetter(BinomialNumberProvider::probability)
	).apply(instance, BinomialNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, BinomialNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::attempts,
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::probability,
		BinomialNumberProvider::new
	);

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.BINOMIAL;
	}

	@Override
	public Number get(Context context) {

		Random random = context.getWorld().getRandom();
		int value = 0;

		int attempts = attempts().get(context.makeChild("attempts")).intValue();
		double probability = probability().get(context.makeChild("probability")).doubleValue();

		for (int i = 0; i < attempts; ++i) {

			if (random.nextDouble() < probability) {
				++value;
			}

		}

		return value;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		attempts().validate(reporter.makeChild("attempts"));
		probability().validate(reporter.makeChild("probability"));
	}

}

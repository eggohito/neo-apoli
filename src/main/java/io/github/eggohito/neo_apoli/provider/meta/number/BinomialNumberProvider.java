package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.random.Random;

public record BinomialNumberProvider(NumberProvider attempts, NumberProvider probability) implements NumberProvider {

	public static final MapCodec<BinomialNumberProvider> CODEC = NeoApoliMapCodecs.lazy(BinomialNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("attempts").forGetter(BinomialNumberProvider::attempts),
		NumberProvider.CODEC.fieldOf("probability").forGetter(BinomialNumberProvider::probability)
	).apply(instance, BinomialNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, BinomialNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(BinomialNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::attempts,
		NumberProvider.PACKET_CODEC, BinomialNumberProvider::probability,
		BinomialNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BINOMIAL;
	}

	@Override
	public double doubleValue(Context context) {

		Random random = context.getWorld().getRandom();
		long result = 0;

		long attempts = attempts().longValue(context.makeChild("attempts"));
		double probability = probability().doubleValue(context.makeChild("probability"));

		for (int i = 0; !context.hasAnyErrors() && i < attempts; i++) {

			if (random.nextDouble() < probability) {
				result++;
			}

		}

		return result;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		attempts().validate(reporter.makeChild("attempts"));
		probability().validate(reporter.makeChild("probability"));

	}

}

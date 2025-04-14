package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public Number get(ValueProviderContext context) {

		Random random = context.getWorld().getRandom();
		int value = 0;

		double chance = probability().get(context).doubleValue();
		int attempts = attempts().get(context).intValue();

		for (int i = 0; i < attempts; ++i) {

			if (random.nextFloat() < chance) {
				++value;
			}

		}

		return value;

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.BINOMIAL;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Stream.concat(attempts().getAllowedParameters().stream(), probability().getAllowedParameters().stream()).collect(Collectors.toSet());
	}

}

package io.github.eggohito.neo_apoli.provider.custom.number;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.MathHelper;

import java.util.Set;

public record UniformNumberProvider(NumberProvider min, NumberProvider max) implements NumberProvider {

	public static final MapCodec<UniformNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("min", new ConstantNumberProvider(0)).forGetter(UniformNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(UniformNumberProvider::max)
	).apply(instance, UniformNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, UniformNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, UniformNumberProvider::min,
		NumberProvider.PACKET_CODEC, UniformNumberProvider::max,
		UniformNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, ValueProviderContext context) {

		double min = min().get(reporter, context).doubleValue();
		double max = max().get(reporter, context).doubleValue();

		return MathHelper.nextDouble(context.getWorld().getRandom(), min, max);

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.UNIFORM;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return ImmutableSet.<ContextParameter<?>>builder()
			.addAll(min().getAllowedParameters())
			.addAll(max().getAllowedParameters())
			.build();
	}

	@Override
	public void validate(ErrorReporter reporter) {
		min().validate(reporter.makeChild("min"));
		max().validate(reporter.makeChild("max"));
	}

}

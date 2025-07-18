package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

public interface CompareToRangeMetaCondition {

	NumberProvider value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@ApiStatus.Internal
	default boolean internalImpl(Context context) {

		Context valueContext = context.makeChild(".value");
		double value = value().nextDouble(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		Context minContext = context.makeChild(".min");
		Optional<Double> min = min().map(minProvider -> minProvider.nextDouble(minContext));

		Context maxContext = context.makeChild(".max");
		Optional<Double> max = max().map(maxProvider -> maxProvider.nextDouble(maxContext));

		if (minContext.hasErrors() || maxContext.hasErrors()) {
			return false;
		}

		else if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
			context.getReporter().report("Minimum value cannot be bigger than maximum value");
			return false;
		}

		else {
			return (min.isEmpty() || !(min.get() > value))
				&& (max.isEmpty() || !(max.get() < value));
		}

	}

	default void validate(ContextAware.ErrorReporter reporter) {

		value().validate(reporter.makeChild(".value"));

		min().ifPresent(minProvider -> minProvider.validate(reporter.makeChild(".min")));
		max().ifPresent(maxProvider -> maxProvider.validate(reporter.makeChild(".max")));

	}

	static <M extends CompareToRangeMetaCondition> MapCodec<M> codec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {

		MapCodec<M> unvalidatedCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NumberProvider.CODEC.fieldOf("value").forGetter(CompareToRangeMetaCondition::value),
			NumberProvider.CODEC.optionalFieldOf("min").forGetter(CompareToRangeMetaCondition::min),
			NumberProvider.CODEC.optionalFieldOf("max").forGetter(CompareToRangeMetaCondition::max)
		).apply(instance, constructor));

		return unvalidatedCodec.validate(m -> {

			if (m.min().isEmpty() && m.max().isEmpty()) {
				return DataResult.error(() -> "Any of 'min' and 'max' fields should be defined!");
			}

			else {
				return DataResult.success(m);
			}

		});

	}

	static <M extends CompareToRangeMetaCondition> PacketCodec<RegistryByteBuf, M> packetCodec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
		return PacketCodec.tuple(
			NumberProvider.PACKET_CODEC, CompareToRangeMetaCondition::value,
			PacketCodecs.optional(NumberProvider.PACKET_CODEC), CompareToRangeMetaCondition::min,
			PacketCodecs.optional(NumberProvider.PACKET_CODEC), CompareToRangeMetaCondition::max,
			constructor
		);
	}

}

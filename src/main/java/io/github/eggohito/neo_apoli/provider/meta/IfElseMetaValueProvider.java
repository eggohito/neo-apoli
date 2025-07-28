package io.github.eggohito.neo_apoli.provider.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IfElseMetaValueProvider<P extends ValueProvider<V>, V> {

	Condition condition();

	P ifValue();

	Optional<P> elseValue();

	@ApiStatus.Internal
	default V internalImpl(Context context, Supplier<V> defaultValue) {
		return internalImpl(context, ValueProvider::next, defaultValue);
	}

	@ApiStatus.Internal
	default <NV> NV internalImpl(Context context, BiFunction<P, Context, NV> getter, Supplier<NV> defaultValue) {

		Context conditionContext = context.makeChild(".condition");
		boolean shouldProvide = condition().test(conditionContext);

		if (!conditionContext.hasErrors()) {

			if (shouldProvide) {
				return getter.apply(ifValue(), context.makeChild(".if_value"));
			}

			else {
				return elseValue().map(elseValue -> getter.apply(elseValue, context.makeChild(".else_value"))).orElseGet(defaultValue);
			}

		}

		else {
			return defaultValue.get();
		}

	}

	default void validate(ContextAware.ErrorReporter reporter) {

		condition().validate(reporter.makeChild(".condition"));

		ifValue().validate(reporter.makeChild(".if_value"));
		elseValue().ifPresent(elseValue -> elseValue.validate(reporter.makeChild(".else_value")));

	}

	static <P extends ValueProvider<V>, V, M extends IfElseMetaValueProvider<P, V>> MapCodec<M> codec(Codec<P> providerCodec, Function3<Condition, P, Optional<P>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Condition.CODEC.fieldOf("condition").forGetter(IfElseMetaValueProvider::condition),
			providerCodec.fieldOf("if_value").forGetter(IfElseMetaValueProvider::ifValue),
			providerCodec.optionalFieldOf("else_value").forGetter(IfElseMetaValueProvider::elseValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends IfElseMetaValueProvider<P, V>> PacketCodec<RegistryByteBuf, M> packetCodec(PacketCodec<RegistryByteBuf, P> providerCodec, Function3<Condition, P, Optional<P>, M> constructor) {
		return PacketCodec.tuple(
			Condition.PACKET_CODEC, IfElseMetaValueProvider::condition,
			providerCodec, IfElseMetaValueProvider::ifValue,
			PacketCodecs.optional(providerCodec), IfElseMetaValueProvider::elseValue,
			constructor
		);
	}

}

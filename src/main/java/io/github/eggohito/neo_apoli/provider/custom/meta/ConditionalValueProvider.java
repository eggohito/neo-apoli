package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface ConditionalValueProvider<P extends ValueProvider<V>, V> extends ValueProvider<V> {

	Condition condition();

	P ifValue();

	P elseValue();

	@ApiStatus.Internal
	default V internalNextOrElse(Context context, Supplier<V> errorValue) {

		Context conditionContext = context.makeChild(".condition");
		boolean shouldProvide = condition().test(conditionContext);

		if (conditionContext.hasErrors()) {
			return errorValue.get();
		}

		else if (shouldProvide) {
			return ifValue().next(context.makeChild(".if_value"));
		}

		else {
			return elseValue().next(context.makeChild(".else_value"));
		}

	}

	@Override
	default void validate(ProblemReporter reporter) {

		ValueProvider.super.validate(reporter);
		condition().validate(reporter.forChild(".condition"));

		ifValue().validate(reporter.forChild(".if_value"));
		elseValue().validate(reporter.forChild(".else_value"));

	}

	static <P extends ValueProvider<V>, V, M extends ConditionalValueProvider<P, V>> MapCodec<M> createCodec(Codec<P> providerCodec, Function3<Condition, P, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Condition.CODEC.fieldOf("condition").forGetter(ConditionalValueProvider::condition),
			providerCodec.fieldOf("if_value").forGetter(ConditionalValueProvider::ifValue),
			providerCodec.fieldOf("else_value").forGetter(ConditionalValueProvider::elseValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider<V>, V, M extends ConditionalValueProvider<P, V>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, Function3<Condition, P, P, M> constructor) {
		return StreamCodec.composite(
			Condition.STREAM_CODEC, ConditionalValueProvider::condition,
			providerCodec, ConditionalValueProvider::ifValue,
			providerCodec, ConditionalValueProvider::elseValue,
			constructor
		);
	}

}

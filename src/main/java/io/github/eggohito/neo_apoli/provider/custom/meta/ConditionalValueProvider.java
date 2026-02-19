package io.github.eggohito.neo_apoli.provider.custom.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ConditionalValueProvider<P extends ContextUser> extends ValueProvider {

	Condition condition();

	P ifValue();

	P elseValue();

	default <V> V nextOrElse(Context context, BiFunction<P, Context, V> getter, Supplier<V> errorValue) {

		Context conditionContext = context.forChild(".condition");
		boolean provides = condition().test(conditionContext);

		if (conditionContext.hasErrors()) {
			return errorValue.get();
		}

		else if (provides) {
			return getter.apply(ifValue(), context.forChild(".if_value"));
		}

		else {
			return getter.apply(elseValue(), context.forChild(".else_value"));
		}

	}

	@Override
	default void validate(Context.Validator validator) {

		ValueProvider.super.validate(validator);
		condition().validate(validator.forChild(".condition"));

		ifValue().validate(validator.forChild(".if_value"));
		elseValue().validate(validator.forChild(".else_value"));

	}

	static <P extends ValueProvider, M extends ConditionalValueProvider<P>> MapCodec<M> mapCodec(Codec<P> providerCodec, Function3<Condition, P, P, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Condition.CODEC.fieldOf("condition").forGetter(ConditionalValueProvider::condition),
			providerCodec.fieldOf("if_value").forGetter(ConditionalValueProvider::ifValue),
			providerCodec.fieldOf("else_value").forGetter(ConditionalValueProvider::elseValue)
		).apply(instance, constructor));
	}

	static <P extends ValueProvider, M extends ConditionalValueProvider<P>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(StreamCodec<RegistryFriendlyByteBuf, P> providerCodec, Function3<Condition, P, P, M> constructor) {
		return StreamCodec.composite(
			Condition.STREAM_CODEC, ConditionalValueProvider::condition,
			providerCodec, ConditionalValueProvider::ifValue,
			providerCodec, ConditionalValueProvider::elseValue,
			constructor
		);
	}

}

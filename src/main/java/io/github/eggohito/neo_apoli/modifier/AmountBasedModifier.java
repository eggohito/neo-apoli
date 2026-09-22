package io.github.eggohito.neo_apoli.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiFunction;

public interface AmountBasedModifier extends Modifier {

	@Override
	Type<?> getType();

	@Override
	default double apply(Context context, double base, double total) {
		return calculate(amount().getFloat(context.forChild(".amount")), base, total);
	}

	@Override
	default void validate(Context.Validator validator) {
		Modifier.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

	FloatProvider amount();

	double calculate(double amount, double base, double total);

	static <M extends AmountBasedModifier> MapCodec<M> mapCodec(BiFunction<Phase, FloatProvider, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> Modifier
			.addPhaseField(instance)
			.and(FloatProvider.CODEC.fieldOf("amount").forGetter(AmountBasedModifier::amount))
			.apply(instance, constructor)
		);
	}

	static <M extends AmountBasedModifier> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(BiFunction<Phase, FloatProvider, M> constructor) {
		return StreamCodec.composite(
			Phase.STREAM_CODEC, AmountBasedModifier::phase,
			FloatProvider.STREAM_CODEC, AmountBasedModifier::amount,
			constructor
		);
	}

}

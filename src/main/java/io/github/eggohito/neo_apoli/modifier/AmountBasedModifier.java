package io.github.eggohito.neo_apoli.modifier;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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

	static <M extends AmountBasedModifier> MapCodec<M> mapCodec(Function3<Phase, Integer, FloatProvider, M> constructor, int defaultOrder) {
		return RecordCodecBuilder.mapCodec(instance -> Modifier
			.addPhaseAndOrderFields(instance, defaultOrder)
			.and(FloatProvider.CODEC.fieldOf("amount").forGetter(AmountBasedModifier::amount))
			.apply(instance, constructor)
		);
	}

	static <M extends AmountBasedModifier> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function3<Phase, Integer, FloatProvider, M> constructor) {
		return StreamCodec.composite(
			Phase.STREAM_CODEC, AmountBasedModifier::phase,
			ByteBufCodecs.INT, AmountBasedModifier::order,
			FloatProvider.STREAM_CODEC, AmountBasedModifier::amount,
			constructor
		);
	}

}

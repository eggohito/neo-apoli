package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface AmountBasedModifier extends Modifier {

	@Override
	ModifierType<?> getType();

	@Override
	default double apply(Context context, double base, double total) {
		return calculate(amount().nextDouble(context.forChild(".amount")), base, total);
	}

	@Override
	default void validate(Context.Validator validator) {
		Modifier.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

	NumberProvider amount();

	double calculate(double amount, double base, double total);

	static <M extends AmountBasedModifier> MapCodec<M> createValueBasedCodec(Function3<Phase, Integer, NumberProvider, M> constructor, int defaultOrder) {
		return RecordCodecBuilder.mapCodec(instance -> Modifier
			.addPhaseAndOrderFields(instance, defaultOrder)
			.and(NumberProvider.CODEC.fieldOf("amount").forGetter(AmountBasedModifier::amount))
			.apply(instance, constructor));
	}

	static <M extends AmountBasedModifier> StreamCodec<RegistryFriendlyByteBuf, M> createValueBasedStreamCodec(Function3<Phase, Integer, NumberProvider, M> constructor) {
		return StreamCodec.composite(
			Phase.STREAM_CODEC, AmountBasedModifier::phase,
			ByteBufCodecs.INT, AmountBasedModifier::order,
			NumberProvider.STREAM_CODEC, AmountBasedModifier::amount,
			constructor
		);
	}

}

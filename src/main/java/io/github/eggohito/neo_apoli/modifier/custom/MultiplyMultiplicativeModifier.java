package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyMultiplicativeModifier(Modifier.Phase phase, int order, FloatProvider amount) implements AmountBasedModifier {

	private static final int DEFAULT_ORDER = 2000;

	public static final MapCodec<MultiplyMultiplicativeModifier> CODEC = AmountBasedModifier.mapCodec(MultiplyMultiplicativeModifier::new, DEFAULT_ORDER);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyMultiplicativeModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MultiplyMultiplicativeModifier::new);

	public MultiplyMultiplicativeModifier(Phase phase, FloatProvider amount) {
		this(phase, DEFAULT_ORDER, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MULTIPLY_MULTIPLICATIVE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total * (1.0 + amount);
	}

}

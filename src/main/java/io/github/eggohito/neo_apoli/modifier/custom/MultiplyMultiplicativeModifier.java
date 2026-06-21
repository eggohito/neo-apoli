package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyMultiplicativeModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyMultiplicativeModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyMultiplicativeModifier::new, 2000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyMultiplicativeModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MultiplyMultiplicativeModifier::new);

	public MultiplyMultiplicativeModifier(Phase phase, ConstantNumberProvider amount) {
		this(phase, 2000, amount);
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

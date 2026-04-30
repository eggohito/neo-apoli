package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.modifier.type.ModifierTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyMultiplicativeModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyMultiplicativeModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyMultiplicativeModifier::new, 2000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyMultiplicativeModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MultiplyMultiplicativeModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY_MULTIPLICATIVE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total * (1.0 + amount);
	}

}

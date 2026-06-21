package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyAdditiveModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyAdditiveModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyAdditiveModifier::new, 1000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyAdditiveModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MultiplyAdditiveModifier::new);

	public MultiplyAdditiveModifier(Phase phase, ConstantNumberProvider amount) {
		this(phase, 1000, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MULTIPLY_ADDITIVE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + (base * amount);
	}

}

package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.modifier.type.ModifierTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyAdditiveModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyAdditiveModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyAdditiveModifier::new, 1000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyAdditiveModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MultiplyAdditiveModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY_ADDITIVE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + (base * amount);
	}

}

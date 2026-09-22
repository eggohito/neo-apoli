package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyAdditiveModifier(Modifier.Phase phase, FloatProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyAdditiveModifier> CODEC = AmountBasedModifier.mapCodec(MultiplyAdditiveModifier::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyAdditiveModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MultiplyAdditiveModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MULTIPLY_ADDITIVE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + (base * amount);
	}

}

package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MultiplyModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyModifier::new, 0);
	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplyModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MultiplyModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MULTIPLY;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total * amount;
	}

}

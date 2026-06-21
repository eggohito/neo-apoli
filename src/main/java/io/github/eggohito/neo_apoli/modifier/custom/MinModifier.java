package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MinModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MinModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MinModifier::new, 5000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MinModifier::new);

	public MinModifier(Phase phase, NumberProvider amount) {
		this(phase, 5000, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MIN;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.min(total, amount);
	}

}

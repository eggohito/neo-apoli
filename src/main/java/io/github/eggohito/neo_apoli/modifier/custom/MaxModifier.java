package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MaxModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MaxModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MaxModifier::new, 6000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MaxModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MaxModifier::new);

	public MaxModifier(Phase phase, NumberProvider amount) {
		this(phase, 6000, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MAX;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.max(total, amount);
	}

}

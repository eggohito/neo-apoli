package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record AddModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<AddModifier> CODEC = AmountBasedModifier.createValueBasedCodec(AddModifier::new, 4000);
	public static final StreamCodec<RegistryFriendlyByteBuf, AddModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(AddModifier::new);

	public AddModifier(Phase phase, NumberProvider amount) {
		this(phase, 4000, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.ADD;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + amount;
	}

}

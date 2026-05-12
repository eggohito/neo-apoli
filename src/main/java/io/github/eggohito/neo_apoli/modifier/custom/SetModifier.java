package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SetModifier(Modifier.Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<SetModifier> CODEC = AmountBasedModifier.createValueBasedCodec(SetModifier::new, 7000);
	public static final StreamCodec<RegistryFriendlyByteBuf, SetModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(SetModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.SET;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return amount;
	}

}

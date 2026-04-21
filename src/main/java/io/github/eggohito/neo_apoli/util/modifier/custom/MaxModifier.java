package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MaxModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MaxModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MaxModifier::new, 6000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MaxModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(MaxModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MAX;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.max(total, amount);
	}

}

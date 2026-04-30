package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.modifier.type.ModifierTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DivideModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<DivideModifier> CODEC = AmountBasedModifier.createValueBasedCodec(DivideModifier::new, 3000);
	public static final StreamCodec<RegistryFriendlyByteBuf, DivideModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(DivideModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.DIVIDE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total / amount;
	}

}

package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MinModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MinModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MinModifier::new, 5000);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedPacketCodec(MinModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MIN;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.min(total, amount);
	}

}

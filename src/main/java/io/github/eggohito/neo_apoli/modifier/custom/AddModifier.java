package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.modifier.type.ModifierTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record AddModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<AddModifier> CODEC = AmountBasedModifier.createValueBasedCodec(AddModifier::new, 4000);
	public static final StreamCodec<RegistryFriendlyByteBuf, AddModifier> STREAM_CODEC = AmountBasedModifier.createValueBasedStreamCodec(AddModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.ADD;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + amount;
	}

}

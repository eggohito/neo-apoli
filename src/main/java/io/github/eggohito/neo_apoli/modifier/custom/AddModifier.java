package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AddModifier(List<Modifier> modifiers, Modifier.Phase phase, FloatProvider amount) implements AmountBasedModifier {

	public static final MapCodec<AddModifier> CODEC = AmountBasedModifier.mapCodec(AddModifier::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, AddModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(AddModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.ADD;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + amount;
	}

}

package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record MaxModifier(List<Modifier> modifiers, Modifier.Phase phase, FloatProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MaxModifier> CODEC = AmountBasedModifier.mapCodec(MaxModifier::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MaxModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MaxModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MAX;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.max(total, amount);
	}

}

package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record MinModifier(List<Modifier> modifiers, Modifier.Phase phase, FloatProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MinModifier> CODEC = AmountBasedModifier.mapCodec(MinModifier::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MinModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MIN;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.min(total, amount);
	}

}

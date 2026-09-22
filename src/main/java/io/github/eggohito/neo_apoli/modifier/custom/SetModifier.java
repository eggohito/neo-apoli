package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SetModifier(List<Modifier> modifiers, Modifier.Phase phase, FloatProvider amount) implements AmountBasedModifier {

	public static final MapCodec<SetModifier> CODEC = AmountBasedModifier.mapCodec(SetModifier::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, SetModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(SetModifier::new);

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.SET;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return amount;
	}

}

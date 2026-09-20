package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SetModifier(Modifier.Phase phase, int order, FloatProvider amount) implements AmountBasedModifier {

	private static final int DEFAULT_ORDER = 7000;

	public static final MapCodec<SetModifier> CODEC = AmountBasedModifier.mapCodec(SetModifier::new, DEFAULT_ORDER);
	public static final StreamCodec<RegistryFriendlyByteBuf, SetModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(SetModifier::new);

	public SetModifier(Phase phase, FloatProvider amount) {
		this(phase, DEFAULT_ORDER, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.SET;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return amount;
	}

}

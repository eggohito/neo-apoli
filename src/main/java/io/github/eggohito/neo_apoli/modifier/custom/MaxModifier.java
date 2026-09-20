package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MaxModifier(Modifier.Phase phase, int order, FloatProvider amount) implements AmountBasedModifier {

	private static final int DEFAULT_ORDER = 6000;

	public static final MapCodec<MaxModifier> CODEC = AmountBasedModifier.mapCodec(MaxModifier::new, DEFAULT_ORDER);
	public static final StreamCodec<RegistryFriendlyByteBuf, MaxModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MaxModifier::new);

	public MaxModifier(Phase phase, FloatProvider amount) {
		this(phase, DEFAULT_ORDER, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MAX;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.max(total, amount);
	}

}

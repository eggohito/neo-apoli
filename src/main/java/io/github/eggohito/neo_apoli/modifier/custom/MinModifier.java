package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MinModifier(Modifier.Phase phase, int order, FloatProvider amount) implements AmountBasedModifier {

	private static final int DEFAULT_ORDER = 5000;

	public static final MapCodec<MinModifier> CODEC = AmountBasedModifier.mapCodec(MinModifier::new, DEFAULT_ORDER);
	public static final StreamCodec<RegistryFriendlyByteBuf, MinModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(MinModifier::new);

	public MinModifier(Phase phase, FloatProvider amount) {
		this(phase, DEFAULT_ORDER, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.MIN;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return Math.min(total, amount);
	}

}

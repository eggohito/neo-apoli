package io.github.eggohito.neo_apoli.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliModifierTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DivideModifier(Modifier.Phase phase, int order, FloatProvider amount) implements AmountBasedModifier {

	private static final int DEFAULT_ORDER = 3000;

	public static final MapCodec<DivideModifier> CODEC = AmountBasedModifier.mapCodec(DivideModifier::new, DEFAULT_ORDER);
	public static final StreamCodec<RegistryFriendlyByteBuf, DivideModifier> STREAM_CODEC = AmountBasedModifier.streamCodec(DivideModifier::new);

	public DivideModifier(Modifier.Phase phase, FloatProvider amount) {
		this(phase, DEFAULT_ORDER, amount);
	}

	@Override
	public Type<?> getType() {
		return NeoApoliModifierTypes.DIVIDE;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total / amount;
	}

}

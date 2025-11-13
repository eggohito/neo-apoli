package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MultiplyMultiplicativeModifier extends SimplePhasedModifier {

	public static final MapCodec<MultiplyMultiplicativeModifier> CODEC = simplePhasedCommonCodec(2000, MultiplyMultiplicativeModifier::new);
	public static final PacketCodec<RegistryByteBuf, MultiplyMultiplicativeModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(MultiplyMultiplicativeModifier::new);

	public MultiplyMultiplicativeModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY_MULTIPLICATIVE;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return total * (1.0 + value);
	}

}

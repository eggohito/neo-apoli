package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MultiplyAdditiveModifier extends SimplePhasedModifier {

	public static final MapCodec<MultiplyAdditiveModifier> CODEC = simplePhasedCommonCodec(1000, MultiplyAdditiveModifier::new);
	public static final PacketCodec<RegistryByteBuf, MultiplyAdditiveModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(MultiplyAdditiveModifier::new);

	public MultiplyAdditiveModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY_ADDITIVE;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return total + (base * value);
	}

}

package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MultiplyModifier extends SimplePhasedModifier {

	public static final MapCodec<MultiplyModifier> CODEC = simplePhasedCommonCodec(0, MultiplyModifier::new);
	public static final PacketCodec<RegistryByteBuf, MultiplyModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(MultiplyModifier::new);

	public MultiplyModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return total * value;
	}

}

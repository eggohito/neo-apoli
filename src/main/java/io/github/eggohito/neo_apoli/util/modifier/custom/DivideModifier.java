package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class DivideModifier extends SimplePhasedModifier {

	public static final MapCodec<DivideModifier> CODEC = simplePhasedCommonCodec(3000, DivideModifier::new);
	public static final PacketCodec<RegistryByteBuf, DivideModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(DivideModifier::new);

	public DivideModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.DIVIDE;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return total / value;
	}

}

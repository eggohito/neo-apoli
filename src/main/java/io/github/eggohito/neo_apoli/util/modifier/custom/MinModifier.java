package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MinModifier extends SimplePhasedModifier {

	public static final MapCodec<MinModifier> CODEC = simplePhasedCommonCodec(5000, MinModifier::new);
	public static final PacketCodec<RegistryByteBuf, MinModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(MinModifier::new);

	public MinModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MIN;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return Math.min(total, value);
	}

}

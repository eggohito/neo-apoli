package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MaxModifier extends SimplePhasedModifier {

	public static final MapCodec<MaxModifier> CODEC = simplePhasedCommonCodec(6000, MaxModifier::new);
	public static final PacketCodec<RegistryByteBuf, MaxModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(MaxModifier::new);

	public MaxModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MAX;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return Math.max(total, value);
	}

}

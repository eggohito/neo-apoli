package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class SetModifier extends SimplePhasedModifier {

	public static final MapCodec<SetModifier> CODEC = simplePhasedCommonCodec(7000, SetModifier::new);
	public static final PacketCodec<RegistryByteBuf, SetModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(SetModifier::new);

	public SetModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.SET;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return value;
	}

}

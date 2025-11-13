package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.SimplePhasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class AddModifier extends SimplePhasedModifier {

	public static final MapCodec<AddModifier> CODEC = simplePhasedCommonCodec(4000, AddModifier::new);
	public static final PacketCodec<RegistryByteBuf, AddModifier> PACKET_CODEC = simplePhasedCommonPacketCodec(AddModifier::new);

	public AddModifier(NumberProvider value, int order, Phase phase) {
		super(value, order, phase);
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.ADD;
	}

	@Override
	protected double calculate(double value, double base, double total) {
		return total + value;
	}

}

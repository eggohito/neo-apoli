package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.ValueBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SetModifier(Phase phase, int order, NumberProvider value) implements ValueBasedModifier {

	public static final MapCodec<SetModifier> CODEC = ValueBasedModifier.createValueBasedCodec(SetModifier::new, 7000);
	public static final PacketCodec<RegistryByteBuf, SetModifier> PACKET_CODEC = ValueBasedModifier.createValueBasedPacketCodec(SetModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.SET;
	}

	@Override
	public double calculate(double value, double base, double total) {
		return value;
	}

}

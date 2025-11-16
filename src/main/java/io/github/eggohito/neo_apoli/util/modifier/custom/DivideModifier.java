package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.ValueBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DivideModifier(Phase phase, int order, NumberProvider value) implements ValueBasedModifier {

	public static final MapCodec<DivideModifier> CODEC = ValueBasedModifier.createValueBasedCodec(DivideModifier::new, 3000);
	public static final PacketCodec<RegistryByteBuf, DivideModifier> PACKET_CODEC = ValueBasedModifier.createValueBasedPacketCodec(DivideModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.DIVIDE;
	}

	@Override
	public double calculate(double value, double base, double total) {
		return total / value;
	}

}

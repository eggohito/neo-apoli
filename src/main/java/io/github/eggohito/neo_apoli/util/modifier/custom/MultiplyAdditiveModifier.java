package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.ValueBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MultiplyAdditiveModifier(Phase phase, int order, NumberProvider value) implements ValueBasedModifier {

	public static final MapCodec<MultiplyAdditiveModifier> CODEC = ValueBasedModifier.createValueBasedCodec(MultiplyAdditiveModifier::new, 1000);
	public static final PacketCodec<RegistryByteBuf, MultiplyAdditiveModifier> PACKET_CODEC = ValueBasedModifier.createValueBasedPacketCodec(MultiplyAdditiveModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY_ADDITIVE;
	}

	@Override
	public double calculate(double value, double base, double total) {
		return total + (base * value);
	}

}

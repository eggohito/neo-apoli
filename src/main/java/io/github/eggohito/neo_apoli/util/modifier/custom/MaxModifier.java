package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.ValueBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MaxModifier(Phase phase, int order, NumberProvider value) implements ValueBasedModifier {

	public static final MapCodec<MaxModifier> CODEC = ValueBasedModifier.createValueBasedCodec(MaxModifier::new, 6000);
	public static final PacketCodec<RegistryByteBuf, MaxModifier> PACKET_CODEC = ValueBasedModifier.createValueBasedPacketCodec(MaxModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MAX;
	}

	@Override
	public double calculate(double value, double base, double total) {
		return Math.max(total, value);
	}

}

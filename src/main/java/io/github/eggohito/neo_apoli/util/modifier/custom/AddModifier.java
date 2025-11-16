package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.ValueBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record AddModifier(Phase phase, int order, NumberProvider value) implements ValueBasedModifier {

	public static final MapCodec<AddModifier> CODEC = ValueBasedModifier.createValueBasedCodec(AddModifier::new, 4000);
	public static final PacketCodec<RegistryByteBuf, AddModifier> PACKET_CODEC = ValueBasedModifier.createValueBasedPacketCodec(AddModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.ADD;
	}

	@Override
	public double calculate(double value, double base, double total) {
		return total + value;
	}

}

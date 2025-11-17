package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SetModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<SetModifier> CODEC = AmountBasedModifier.createValueBasedCodec(SetModifier::new, 7000);
	public static final PacketCodec<RegistryByteBuf, SetModifier> PACKET_CODEC = AmountBasedModifier.createValueBasedPacketCodec(SetModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.SET;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return amount;
	}

}

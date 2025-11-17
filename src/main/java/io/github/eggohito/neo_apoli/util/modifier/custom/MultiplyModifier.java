package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record MultiplyModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<MultiplyModifier> CODEC = AmountBasedModifier.createValueBasedCodec(MultiplyModifier::new, 0);
	public static final PacketCodec<RegistryByteBuf, MultiplyModifier> PACKET_CODEC = AmountBasedModifier.createValueBasedPacketCodec(MultiplyModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.MULTIPLY;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total * amount;
	}

}

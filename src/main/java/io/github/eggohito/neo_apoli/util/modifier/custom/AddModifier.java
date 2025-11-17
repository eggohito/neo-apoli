package io.github.eggohito.neo_apoli.util.modifier.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.AmountBasedModifier;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record AddModifier(Phase phase, int order, NumberProvider amount) implements AmountBasedModifier {

	public static final MapCodec<AddModifier> CODEC = AmountBasedModifier.createValueBasedCodec(AddModifier::new, 4000);
	public static final PacketCodec<RegistryByteBuf, AddModifier> PACKET_CODEC = AmountBasedModifier.createValueBasedPacketCodec(AddModifier::new);

	@Override
	public ModifierType<?> getType() {
		return ModifierTypes.ADD;
	}

	@Override
	public double calculate(double amount, double base, double total) {
		return total + amount;
	}

}

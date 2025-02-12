package io.github.eggohito.neo_apoli.network.codec;

import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.network.RegistryByteBuf;

@FunctionalInterface
public interface PowerPacketEncoder<P extends Power> {
	void encode(RegistryByteBuf buf, P power);
}

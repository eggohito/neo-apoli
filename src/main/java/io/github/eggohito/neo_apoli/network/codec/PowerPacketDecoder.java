package io.github.eggohito.neo_apoli.network.codec;

import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.network.RegistryByteBuf;

@FunctionalInterface
public interface PowerPacketDecoder<P extends Power> {
	P decode(RegistryByteBuf buf, Power.Metadata metadata);
}

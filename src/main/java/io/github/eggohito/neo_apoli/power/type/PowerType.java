package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record PowerType<P extends Power>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {

}

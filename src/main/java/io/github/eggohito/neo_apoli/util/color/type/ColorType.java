package io.github.eggohito.neo_apoli.util.color.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.color.Color;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ColorType<C extends Color>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {

}

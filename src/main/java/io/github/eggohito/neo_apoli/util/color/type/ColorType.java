package io.github.eggohito.neo_apoli.util.color.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.color.Color;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ColorType<C extends Color>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {

}

package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NumberProviderType<P extends NumberProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {

}

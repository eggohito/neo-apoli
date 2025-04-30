package io.github.eggohito.neo_apoli.provider.type.string;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record StringProviderType<P extends StringProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {

}

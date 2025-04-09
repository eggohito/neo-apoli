package io.github.eggohito.neo_apoli.provider.type.strings;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.StringValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record StringValueProviderType<P extends StringValueProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

}

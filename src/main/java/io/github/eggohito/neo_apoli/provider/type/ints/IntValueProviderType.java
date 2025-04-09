package io.github.eggohito.neo_apoli.provider.type.ints;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.IntValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IntValueProviderType<P extends IntValueProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

}

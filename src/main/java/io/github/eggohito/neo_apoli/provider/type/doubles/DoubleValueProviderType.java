package io.github.eggohito.neo_apoli.provider.type.doubles;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.DoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DoubleValueProviderType<P extends DoubleValueProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

}

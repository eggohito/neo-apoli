package io.github.eggohito.neo_apoli.provider.type.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record BooleanProviderType<P extends BooleanProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

}

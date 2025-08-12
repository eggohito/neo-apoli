package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record BoxProviderType<B extends BoxProvider>(MapCodec<B> mapCodec, PacketCodec<RegistryByteBuf, B> packetCodec) implements ValueProviderType<B> {

}

package io.github.eggohito.neo_apoli.util.modifier.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ModifierType<M extends Modifier>(MapCodec<M> mapCodec, PacketCodec<RegistryByteBuf, M> packetCodec) {

}

package io.github.eggohito.neo_apoli.util.modifier.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ModifierType<M extends Modifier>(MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> packetCodec) {

}

package io.github.eggohito.neo_apoli.util.comparison.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ComparisonType<C extends Comparison>(MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {

}

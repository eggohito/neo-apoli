package io.github.eggohito.neo_apoli.util.comparison.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ComparisonType<C extends Comparison>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {

}

package io.github.eggohito.neo_apoli.provider.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ValueProviderType<P extends ValueProvider> {

	MapCodec<P> mapCodec();

	PacketCodec<? extends ByteBuf, P> packetCodec();

}

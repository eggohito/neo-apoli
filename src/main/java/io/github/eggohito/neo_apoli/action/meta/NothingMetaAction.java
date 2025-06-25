package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Supplier;

public interface NothingMetaAction {

	static <M extends NothingMetaAction> MapCodec<M> codec(Supplier<M> constructor) {
		return MapCodec.unit(constructor);
	}

	static <B extends ByteBuf, M extends NothingMetaAction> PacketCodec<B, M> packetCodec(Supplier<M> constructor) {
		return PacketCodec.unit(constructor.get());
	}

}

package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum Environment {

	CLIENT,
	SERVER;

	public static final Codec<Environment> CODEC = CodecUtil.enumType(Environment.class);
	public static final StreamCodec<ByteBuf, Environment> STREAM_CODEC = StreamCodecUtil.enumType(Environment.class);

}

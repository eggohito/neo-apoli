package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum NumberType {

	DOUBLE,
	FLOAT,
	LONG,
	INT,
	SHORT,
	BYTE;

	public static final Codec<NumberType> CODEC = CodecUtil.enumType(NumberType.class);
	public static final StreamCodec<ByteBuf, NumberType> STREAM_CODEC = StreamCodecUtil.enumType(NumberType.class);

}

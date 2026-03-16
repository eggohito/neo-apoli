package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum PrimitiveNumberType {

	DOUBLE,
	FLOAT,
	LONG,
	INT,
	SHORT,
	BYTE;

	public static final Codec<PrimitiveNumberType> CODEC = CodecUtil.enumType(PrimitiveNumberType.class);
	public static final StreamCodec<ByteBuf, PrimitiveNumberType> STREAM_CODEC = StreamCodecUtil.enumType(PrimitiveNumberType.class);

}

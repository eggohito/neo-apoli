package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.OptionalInt;

public enum TextAlignment implements StringRepresentable {

	NONE("none", (left, right, textWidth) -> OptionalInt.empty()),
	LEFT("left", (left, right, textWidth) -> OptionalInt.of(left - 1)),
	RIGHT("right", (left, right, textWidth) -> OptionalInt.of(right - textWidth + 1)),
	CENTER("center", (left, right, textWidth) -> OptionalInt.of((left + right - textWidth) / 2));

	public static final Codec<TextAlignment> CODEC = CodecUtil.enumType(TextAlignment.class);
	public static final StreamCodec<ByteBuf, TextAlignment> STREAM_CODEC = StreamCodecUtil.enumType(TextAlignment.class);

	final String name;
	final HorizontalPosition horizontal;

	TextAlignment(String name, HorizontalPosition horizontal) {
		this.name = name;
		this.horizontal = horizontal;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public OptionalInt horizontal(int left, int right, int textWidth) {
		return horizontal.apply(left, right, textWidth);
	}

	@FunctionalInterface
	public interface HorizontalPosition {
		OptionalInt apply(int left, int right, int textWidth);
	}

}

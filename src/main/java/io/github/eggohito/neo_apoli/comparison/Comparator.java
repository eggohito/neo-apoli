package io.github.eggohito.neo_apoli.comparison;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Comparator implements StringRepresentable {

	EQUALS("=="),
	NOT_EQUALS("!="),
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL("<=");

	public static final Codec<Comparator> CODEC = CodecUtil.enumType(Comparator.class);
	public static final StreamCodec<ByteBuf, Comparator> STREAM_CODEC = StreamCodecUtil.enumType(Comparator.class);

	final String stringForm;
	Comparator(String stringForm) {
		this.stringForm = stringForm;
	}

	@Override
	public @NotNull String getSerializedName() {
		return stringForm;
	}

	public <T extends Comparable<T>> boolean compare(T first, T second) {
		return switch (this) {
			case EQUALS ->
				first.compareTo(second) == 0;
			case NOT_EQUALS ->
				first.compareTo(second) != 0;
			case GREATER_THAN ->
				first.compareTo(second) > 0;
			case GREATER_THAN_OR_EQUAL ->
				first.compareTo(second) >= 0;
			case LESS_THAN ->
				first.compareTo(second) < 0;
			case LESS_THAN_OR_EQUAL ->
				first.compareTo(second) <= 0;
		};
	}

}

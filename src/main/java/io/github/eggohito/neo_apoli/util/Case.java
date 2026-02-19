package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Case<C, V>(C condition, V value) {

	public static <C, V> Codec<Case<C, V>> codec(Codec<C> conditionCodec, Codec<V> valueCodec) {
		return codec(conditionCodec.fieldOf("condition"), valueCodec.fieldOf("value"));
	}

	public static <C, V> Codec<Case<C, V>> codec(MapCodec<C> conditionCodec, MapCodec<V> valueCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
			conditionCodec.forGetter(Case::condition),
			valueCodec.forGetter(Case::value)
		).apply(instance, Case::new));
	}

	public static <B extends ByteBuf, C, V> StreamCodec<B, Case<C, V>> streamCodec(StreamCodec<B, C> conditionCodec, StreamCodec<B, V> valueCodec) {
		return StreamCodec.composite(
			conditionCodec, Case::condition,
			valueCodec, Case::value,
			Case::new
		);
	}

}

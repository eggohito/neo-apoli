package io.github.eggohito.neo_apoli.util.conditional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public interface CompositeConditional<V> {

	List<Entry<V>> entries();

	V defaultValue();

	record Entry<V>(Condition condition, V value) {

		public static <V> Codec<Entry<V>> codec(Codec<Condition> conditionCodec, Codec<V> valueCodec) {
			return codec(conditionCodec.fieldOf("condition"), valueCodec.fieldOf("value"));
		}

		public static <V> Codec<Entry<V>> codec(MapCodec<Condition> conditionCodec, MapCodec<V> valueCodec) {
			return RecordCodecBuilder.create(instance -> instance.group(
				conditionCodec.forGetter(Entry::condition),
				valueCodec.forGetter(Entry::value)
			).apply(instance, Entry::new));
		}

		public static <B extends ByteBuf, V> StreamCodec<B, Entry<V>> streamCodec(StreamCodec<B, Condition> conditionCodec, StreamCodec<B, V> valueCodec) {
			return StreamCodec.composite(
				conditionCodec, Entry::condition,
				valueCodec, Entry::value,
				Entry::new
			);
		}

	}

}

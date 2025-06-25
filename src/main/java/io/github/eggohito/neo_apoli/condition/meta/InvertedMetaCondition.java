package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface InvertedMetaCondition<C extends Condition> {

	C condition();

	default boolean impl(Context context) {
		return !condition().test(context.makeChild(".condition"));
	}

	default void validate(ContextAware.ErrorReporter reporter) {
		condition().validate(reporter.makeChild(".condition"));
	}

	static <C extends Condition, M extends InvertedMetaCondition<C>> MapCodec<M> codec(Codec<C> elementCodec, Function<C, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.fieldOf("condition").forGetter(InvertedMetaCondition::condition)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, C extends Condition, M extends InvertedMetaCondition<C>> PacketCodec<B, M> packetCodec(PacketCodec<B, C> elementCodec, Function<C, M> constructor) {
		return elementCodec.xmap(constructor, InvertedMetaCondition::condition);
	}

}

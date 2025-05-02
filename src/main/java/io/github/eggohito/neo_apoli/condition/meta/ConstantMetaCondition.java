package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ConstantMetaCondition<T extends ConditionType<?>> extends Condition<T> {

	@Override
	default boolean test(Context context) {
		return value();
	}

	@Override
	default void validate(ErrorReporter reporter) {

	}

	boolean value();

	static <M extends ConstantMetaCondition<?>> MapCodec<M> createCodec(Boolean2ObjectFunction<M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("value").forGetter(ConstantMetaCondition::value)
		).apply(instance, constructor));
	}

	static <M extends ConstantMetaCondition<?>> PacketCodec<ByteBuf, M> createPacketCodec(Boolean2ObjectFunction<M> constructor) {
		return PacketCodecs.BOOLEAN.xmap(constructor, ConstantMetaCondition::value);
	}

}

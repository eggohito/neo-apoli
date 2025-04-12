package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public interface ConstantMetaCondition<CX extends ConditionContext, CT extends ConditionType<?>> extends Condition<CX, CT> {

	@Override
	default boolean test(CX context) {
		return value();
	}

	boolean value();

	static <CM extends ConstantMetaCondition<?, ?>> MapCodec<CM> createCodec(Boolean2ObjectFunction<CM> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("value").forGetter(ConstantMetaCondition::value)
		).apply(instance, constructor));
	}

	static <CM extends ConstantMetaCondition<?, ?>> PacketCodec<ByteBuf, CM> createPacketCodec(Boolean2ObjectFunction<CM> constructor) {
		return PacketCodecs.BOOLEAN.xmap(constructor, ConstantMetaCondition::value);
	}

}

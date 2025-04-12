package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface InvertedMetaCondition<CX extends ConditionContext, CT extends ConditionType<?>, CC extends Condition<CX, CT>> extends Condition<CX, CT> {

	@Override
	default boolean test(CX context) {
		return !condition().test(context);
	}

	CC condition();

	static <CC extends Condition<?, ?>, IC extends InvertedMetaCondition<?, ?, CC>> MapCodec<IC> createCodec(Codec<CC> elementCodec, Function<CC, IC> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			elementCodec.fieldOf("condition").forGetter(InvertedMetaCondition::condition)
		).apply(instance, constructor));
	}

	static <BB extends ByteBuf, CC extends Condition<?, ?>, IC extends InvertedMetaCondition<?, ?, CC>> PacketCodec<BB, IC> createPacketCodec(PacketCodec<BB, CC> elementCodec, Function<CC, IC> constructor) {
		return elementCodec.xmap(constructor, InvertedMetaCondition::condition);
	}

}

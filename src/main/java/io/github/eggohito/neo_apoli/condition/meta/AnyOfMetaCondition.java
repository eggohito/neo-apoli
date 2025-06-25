package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;
import java.util.function.Function;

public interface AnyOfMetaCondition<C extends Condition> extends MultiMetaCondition<C> {

	default boolean impl(Context context) {
		return this.iterateAndProcess(context, Boolean::logicalOr, result -> !result, true);
	}

	static <C extends Condition, M extends AnyOfMetaCondition<C>> MapCodec<M> codec(Codec<C> conditionCodec, Function<List<C>, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> MultiMetaCondition.addConditionsField(conditionCodec, instance).apply(instance, constructor));
	}

	static <B extends ByteBuf, C extends Condition, M extends AnyOfMetaCondition<C>> PacketCodec<B, M> packetCodec(PacketCodec<B, C> conditionCodec, Function<List<C>, M> constructor) {
		return MultiMetaCondition.createConditionsPacketCodec(conditionCodec, (buf, m) -> {}, (buf, conditions) -> constructor.apply(conditions));
	}

}

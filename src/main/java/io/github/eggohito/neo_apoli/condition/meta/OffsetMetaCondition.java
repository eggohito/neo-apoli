package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;

public interface OffsetMetaCondition<C extends Condition> {

	C condition();

	Vec3d offset();

	default void validate(ContextAware.ErrorReporter reporter) {
		condition().validate(reporter.makeChild(".condition"));
	}

	static <C extends Condition, M extends OffsetMetaCondition<C>> MapCodec<M> codec(Codec<C> conditionCodec, BiFunction<C, Vec3d, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			conditionCodec.fieldOf("condition").forGetter(OffsetMetaCondition::condition),
			Vec3d.CODEC.fieldOf("offset").forGetter(OffsetMetaCondition::offset)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, C extends Condition, M extends OffsetMetaCondition<C>> PacketCodec<B, M> packetCodec(PacketCodec<B, C> conditionCodec, BiFunction<C, Vec3d, M> constructor) {
		return PacketCodec.tuple(
			conditionCodec, OffsetMetaCondition::condition,
			Vec3d.PACKET_CODEC, OffsetMetaCondition::offset,
			constructor
		);
	}

}

package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;

public interface OffsetMetaAction<A extends Action> {

	A action();

	Vec3d offset();

	default void validate(ContextAware.ErrorReporter reporter) {
		action().validate(reporter.makeChild(".action"));
	}

	static <A extends Action, M extends OffsetMetaAction<A>> MapCodec<M> codec(Codec<A> actionCodec, BiFunction<A, Vec3d, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("action").forGetter(OffsetMetaAction::action),
			Vec3d.CODEC.fieldOf("offset").forGetter(OffsetMetaAction::offset)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action, M extends OffsetMetaAction<A>> PacketCodec<B, M> packetCodec(PacketCodec<B, A> actionCodec, BiFunction<A, Vec3d, M> constructor) {
		return PacketCodec.tuple(
			actionCodec, OffsetMetaAction::action,
			Vec3d.PACKET_CODEC, OffsetMetaAction::offset,
			constructor
		);
	}

}

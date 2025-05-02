package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;
import java.util.function.BiFunction;

public interface OffsetMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	A action();

	Vec3d offset();

	@Override
	default void execute(Context context) {

		Vec3d offsetPosition = context.requiredParameter(ContextParameters.POSITION).add(offset());
		Context contextCopy = Context.copy(context , builder -> builder.add(ContextParameters.POSITION, offsetPosition));

		action().execute(contextCopy.makeChild("action"));

	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	static <A extends Action<?>, M extends OffsetMetaAction<A, ?>> MapCodec<M> createCodec(Codec<A> actionCodec, BiFunction<A, Vec3d, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("action").forGetter(OffsetMetaAction::action),
			Vec3d.CODEC.fieldOf("offset").forGetter(OffsetMetaAction::offset)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?>, M extends OffsetMetaAction<A, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, A> actionCodec, BiFunction<A, Vec3d, M> constructor) {
		return PacketCodec.tuple(
			actionCodec, OffsetMetaAction::action,
			Vec3d.PACKET_CODEC, OffsetMetaAction::offset,
			constructor
		);
	}

}

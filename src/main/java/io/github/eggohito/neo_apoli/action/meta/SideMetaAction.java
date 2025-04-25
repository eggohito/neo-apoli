package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.BiFunction;

public interface SideMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	A action();

	Side side();

	@Override
	default void execute(Context context) {

		if ((side() == Side.CLIENT) != NeoApoli.serverSide()) {
			action().execute(context.makeChild("action"));
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {
		action().validate(reporter.makeChild("action"));
	}

	static <A extends Action<?>, M extends SideMetaAction<A, ?>> MapCodec<M> createCodec(Codec<A> actionCodec, BiFunction<A, Side, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("action").forGetter(SideMetaAction::action),
			Side.CODEC.fieldOf("side").forGetter(SideMetaAction::side)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, A extends Action<?>, M extends SideMetaAction<A, ?>> PacketCodec<B, M> createPacketCodec(PacketCodec<B, A> actionCodec, BiFunction<A, Side, M> constructor) {
		return PacketCodec.tuple(
			actionCodec, SideMetaAction::action,
			Side.PACKET_CODEC, SideMetaAction::side,
			constructor
		);
	}

	enum Side implements StringIdentifiable {

		SERVER("server"),
		CLIENT("client");

		public static final Codec<Side> CODEC = StringIdentifiable.createBasicCodec(Side::values);
		public static final PacketCodec<ByteBuf, Side> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIndexToValueFunction(Side::ordinal, Side.values(), ValueLists.OutOfBoundsHandling.WRAP), Side::ordinal);

		final String name;
		Side(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

	}

}

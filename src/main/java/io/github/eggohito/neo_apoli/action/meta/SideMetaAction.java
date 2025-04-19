package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.BiFunction;

public interface SideMetaAction<AX extends ActionContext<?>, AA extends Action<AX, AT>, AT extends ActionType<?>> extends Action<AX, AT> {

	AA action();

	Side side();

	@Override
	default void execute(ErrorReporter reporter, AX context) {

		if ((side() == Side.CLIENT) != NeoApoli.serverSide()) {
			action().execute(reporter, context);
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {
		action().validate(reporter.makeChild("action"));
	}

	static <AA extends Action<?, ?>, MA extends SideMetaAction<?, AA, ?>> MapCodec<MA> createCodec(Codec<AA> actionCodec, BiFunction<AA, Side, MA> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			actionCodec.fieldOf("action").forGetter(SideMetaAction::action),
			Side.CODEC.fieldOf("side").forGetter(SideMetaAction::side)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, AA extends Action<?, ?>, MA extends SideMetaAction<?, AA, ?>> PacketCodec<B, MA> createPacketCodec(PacketCodec<B, AA> actionCodec, BiFunction<AA, Side, MA> constructor) {
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

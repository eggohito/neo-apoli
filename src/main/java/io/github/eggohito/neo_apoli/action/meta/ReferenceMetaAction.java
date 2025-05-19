package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface ReferenceMetaAction<A extends Action<T>, T extends ActionType<?>> extends Action<T> {

	@Override
	ActionCategory<A> getCategory();

	@Override
	default void execute(Context context) {

		Identifier value = this.value();
		ActionCategory<A> category = this.getCategory();

		ActionManager.getAsResult(category, value)
			.ifError(error -> context.getReporter().report(error.message()))
			.ifSuccess(action -> {

				if (context.markActive(action)) {

					try {
						action.execute(context);
					}

					finally {
						context.markInactive(action);
					}

				}

				else {
					context.getReporter().report(category + " \"" + value + "\" was recursively referenced!");
				}

			});

	}

	@Override
	default void validate(ErrorReporter reporter) {

		Identifier value = this.value();
		ActionCategory<A> category = this.getCategory();

		if (reporter.isInStack(value)) {
			reporter.report(category + " \"" + value + "\" was recursively referenced!");
		}

		else {
			ActionManager.getAsResult(category, value)
				.ifSuccess(action -> action.validate(reporter.makeChild("{\"" + value + "\"}", value)))
				.ifError(error -> reporter.report(error.message()));
		}

	}

	Identifier value();

	static <M extends ReferenceMetaAction<?, ?>> MapCodec<M> codec(Function<Identifier, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("value").forGetter(ReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, M extends ReferenceMetaAction<?, ?>> PacketCodec<B, M> packetCodec(Function<Identifier, M> constructor) {
		return PacketCodec.tuple(
			Identifier.PACKET_CODEC, ReferenceMetaAction::value,
			constructor
		);
	}

}

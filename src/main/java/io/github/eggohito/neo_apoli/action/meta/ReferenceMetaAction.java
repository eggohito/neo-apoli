package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public interface ReferenceMetaAction<A extends Action> {

	ActionCategory<A> getCategory();

	@ApiStatus.Internal
	default void internalImpl(Context context) {

		Identifier value = this.value();
		ActionCategory<A> category = this.getCategory();

		ActionManager.getAsResult(category, value)
			.ifError(error -> context.getReporter().report(error.message()))
			.ifSuccess(action -> {

				if (context.markActive(action)) {

					try {
						action.execute(context.makeChild("{" + value() + "}", value()));
					}

					finally {
						context.markInactive(action);
					}

				}

				else {
					context.makeChild("{" + value + "}", value).getReporter().report(category + " \"" + value + "\" was recursively referenced!");
				}

			});

	}

	default void validate(ContextAware.ErrorReporter reporter) {

		Identifier value = this.value();
		ActionCategory<A> category = this.getCategory();

		if (reporter.isInStack(value)) {
			reporter.makeChild(".value").report(category + " \"" + value + "\" was recursively referenced!");
		}

		else {
			ActionManager.getAsResult(category, value)
				.ifSuccess(action -> action.validate(reporter.makeChild("{" + value + "}", value)))
				.ifError(error -> reporter.makeChild(".value").report(error.message()));
		}

	}

	Identifier value();

	static <M extends ReferenceMetaAction<?>> MapCodec<M> codec(Function<Identifier, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("value").forGetter(ReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <B extends ByteBuf, M extends ReferenceMetaAction<?>> PacketCodec<B, M> packetCodec(Function<Identifier, M> constructor) {
		return PacketCodec.tuple(
			Identifier.PACKET_CODEC, ReferenceMetaAction::value,
			constructor
		);
	}

}

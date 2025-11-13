package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface ReferenceMetaAction<A extends Action> extends MetaAction {

	Pair<Class<A>, String> classAndName();

	Identifier value();

	@Override
	default void execute(Context context) {

		ActionManager.getAsResult(this.value())
			.flatMap(this::checkAndCast)
			.ifSuccess(
				action -> {

					try {

						if (context.markActive(action)) {
							action.execute(context.makeChild("{" + this.value() + "}", this.value()));
						}

						else {
							context.getReporter().makeChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was executed recursively!");
						}

					}

					finally {
						context.markInActive(action);
					}

				}
			);

	}

	@Override
	default void validate(ErrorReporter reporter) {

		if (reporter.isInStack(this.value())) {
			reporter.makeChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ActionManager.getAsResult(this.value())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(reporter.makeChild("{" + this.value() + "}", this.value())))
				.ifError(error -> reporter.makeChild(".value").report(error.message()));
		}

	}

	default DataResult<A> checkAndCast(Action action) {

		Class<A> actionClass = this.classAndName().getFirst();
		String name = this.classAndName().getSecond();

		if (actionClass.isInstance(action)) {
			return DataResult.success(actionClass.cast(action));
		}

		else {
			return DataResult.error(() -> name + " with ID \"" + this.value() + "\" doesn't exist!");
		}

	}

	static <A extends Action, M extends ReferenceMetaAction<A>> MapCodec<M> codec(Function<Identifier, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("value").forGetter(ReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends ReferenceMetaAction<A>> PacketCodec<RegistryByteBuf, M> packetCodec(Function<Identifier, M> constructor) {
		return PacketCodec.tuple(
			Identifier.PACKET_CODEC, ReferenceMetaAction::value,
			constructor
		);
	}

}

package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface ReferenceMetaAction<A extends Action> extends MetaAction {

	Pair<Class<A>, String> classAndName();

	ResourceLocation value();

	@Override
	default void execute(Context context) {

		ActionManager.getAsResult(this.value())
			.flatMap(this::checkAndCast)
			.ifSuccess(
				action -> {

					try {

						if (context.markActive(action)) {
							action.execute(context.forChildWithReference("{" + this.value() + "}", this.value()));
						}

						else {
							context.getValidator().forChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was executed recursively!");
						}

					}

					finally {
						context.markInActive(action);
					}

				}
			);

	}

	@Override
	default void validate(Context.Validator validator) {

		if (validator.isReferenced(this.value())) {
			validator.forChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ActionManager.getAsResult(this.value())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(validator.forChildWithReference("{\"" + this.value() + "\"}", this.value())))
				.ifError(error -> validator.forChild(".value").report(error.message()));
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

	static <A extends Action, M extends ReferenceMetaAction<A>> MapCodec<M> createCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			DynamicResourceLocation.CODEC.fieldOf("value").forGetter(ReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends ReferenceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ReferenceMetaAction::value,
			constructor
		);
	}

}

package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface IReferenceMetaAction<A extends Action> extends MetaAction {

	Pair<Class<A>, String> classAndName();

	ResourceLocation value();

	@Override
	default void execute(Context context) {

		ActionManager.getAsResult(this.value())
			.flatMap(this::checkAndCast)
			.ifSuccess(
				action -> {

					try {

						if (context.visitor().push(action)) {
							action.execute(context.forChild(".{\"" + this.value() + "\"}"));
						}

						else {
							context.forChild(".value").reportProblem(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was executed recursively!");
						}

					}

					finally {
						context.visitor().pop(action);
					}

				}
			);

	}

	@Override
	default void validate(Context.Validator validator) {

		ResourceKey<Action> key = ResourceKey.create(NeoApoliRegistryKeys.ACTION, this.value());
		Context.Validator valueValidator = validator.forChild(".value");

		if (validator.hasVisited(key)) {
			valueValidator.reportProblem(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ActionManager.getAsResult(this.value())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(validator.visitChild(".value", key)))
				.ifError(error -> valueValidator.reportProblem(error.message()));
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

	static <A extends Action, M extends IReferenceMetaAction<A>> MapCodec<M> mapCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("value").forGetter(IReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends IReferenceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, IReferenceMetaAction::value,
			constructor
		);
	}

}

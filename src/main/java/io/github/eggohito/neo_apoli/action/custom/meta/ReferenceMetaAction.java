package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface ReferenceMetaAction<A extends Action> extends Action {

	ActionKind<A> targetKind();

	ResourceLocation value();

	@Override
	default void execute(Context context) {
		ActionManager.getAsResult(this.targetKind(), this.value()).ifSuccess(action -> {

			try {

				if (context.visitor().push(action)) {
					action.execute(context.forChild(".{\"" + this.value() + "\"}"));
				}

				else {
					context.forChild(".value").reportProblem(this.targetKind().asDisplayString() + " with ID \"" + this.value() + "\" was executed recursively!");
				}

			}

			finally {
				context.visitor().pop(action);
			}

		});
	}

	@Override
	default void validate(Context.Validator validator) {

		Action.super.validate(validator);

		ResourceKey<A> key = ResourceKey.create(this.targetKind().registryKey(), this.value());
		Context.Validator valueValidator = validator.forChild(".value");

		if (validator.hasVisited(key)) {
			valueValidator.reportProblem(this.targetKind().asDisplayString() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ActionManager.getAsResult(this.targetKind(), this.value())
				.ifSuccess(action -> action.validate(validator.visitChild(".{\"" + this.value() + "\"}", key)))
				.ifError(error -> valueValidator.reportProblem(error.message()));
		}

	}

	static <A extends Action, M extends ReferenceMetaAction<A>> MapCodec<M> mapCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("value").forGetter(ReferenceMetaAction::value)
		).apply(instance, constructor));
	}

	static <A extends Action, M extends ReferenceMetaAction<A>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ReferenceMetaAction::value,
			constructor
		);
	}

}

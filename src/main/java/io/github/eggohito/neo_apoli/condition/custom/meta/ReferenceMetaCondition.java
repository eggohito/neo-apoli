package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface ReferenceMetaCondition<C extends Condition> extends Condition {

	ResourceLocation value();

	ConditionKind<C> targetCategory();

	@Override
	default boolean test(Context context) {
		return ConditionManager.getAsResult(this.targetCategory(), this.value()).mapOrElse(
			condition -> {

				try {

					if (context.visitor().push(condition)) {
						return condition.test(context.forChild(".{\"" + this.value() + "\"}"));
					}

					else {
						context.forChild(".value").reportProblem(this.targetCategory().asDisplayString() + " with ID \"" + this.value() + "\" was tested recursively!");
					}

				}

				finally {
					context.visitor().pop(condition);
				}

				return false;

			},
			error -> false
		);
	}

	@Override
	default void validate(Context.Validator validator) {

		Condition.super.validate(validator);

		ResourceKey<C> key = ResourceKey.create(this.targetCategory().registryKey(), this.value());
		Context.Validator valueValidator = validator.forChild(".value");

		if (validator.hasVisited(key)) {
			valueValidator.reportProblem(this.targetCategory().asDisplayString() + " with ID \"" + this.value() + " was referenced recursively!");
		}

		else {
			ConditionManager.getAsResult(this.targetCategory(), this.value())
				.ifSuccess(condition -> condition.validate(validator.visitChild(".{\"" + this.value() + "\"}", key)))
				.ifError(error -> valueValidator.reportProblem(error.message()));
		}

	}

	static <C extends Condition, M extends ReferenceMetaCondition<C>> MapCodec<M> mapCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("value").forGetter(ReferenceMetaCondition::value)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends ReferenceMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ReferenceMetaCondition::value,
			constructor
		);
	}

}

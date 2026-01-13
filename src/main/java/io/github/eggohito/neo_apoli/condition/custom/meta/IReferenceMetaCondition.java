package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface IReferenceMetaCondition<C extends Condition> extends MetaCondition {

	ResourceLocation value();

	Pair<Class<C>, String> classAndName();

	@Override
	default boolean test(Context context) {
		return ConditionManager.getAsResult(this.value())
			.flatMap(this::checkAndCast)
			.mapOrElse(
				condition -> {

					try {

						if (context.markActive(condition)) {
							return condition.test(context.forChildWithReference("{" + this.value() + "}", this.value()));
						}

						else {
							context.getValidator().forChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was tested recursively!");
						}

					}

					finally {
						context.markInActive(condition);
					}

					return false;

				},
				error -> false
			);

	}

	@Override
	default void validate(Context.Validator validator) {

		if (validator.isReferenced(this.value())) {
			validator.forChild(".value").report(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was referenced recursively!");
		}

		else {
			ConditionManager.getAsResult(this.value())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(validator.forChildWithReference("{" + this.value() + "}", this.value())))
				.ifError(error -> validator.forChild(".value").report(error.message()));
		}

	}

	default DataResult<C> checkAndCast(Condition condition) {

		Class<C> clazz = this.classAndName().getFirst();
		String name = this.classAndName().getSecond();

		if (clazz.isInstance(condition)) {
			return DataResult.success(clazz.cast(condition));
		}

		else {
			return DataResult.error(() -> name + " with ID \"" + this.value() + "\" doesn't exist!");
		}

	}

	static <C extends Condition, M extends IReferenceMetaCondition<C>> MapCodec<M> createCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("value").forGetter(IReferenceMetaCondition::value)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends IReferenceMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> createStreamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, IReferenceMetaCondition::value,
			constructor
		);
	}

}

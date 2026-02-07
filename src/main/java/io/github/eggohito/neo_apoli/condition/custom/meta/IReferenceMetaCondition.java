package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
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

						if (context.visitor().push(condition)) {
							return condition.test(context.forChild("{" + this.value() + "}"));
						}

						else {
							context.forChild(".value").reportProblem(this.classAndName().getSecond() + " with ID \"" + this.value() + "\" was tested recursively!");
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

		ResourceKey<Condition> key = ResourceKey.create(NeoApoliRegistryKeys.CONDITION, this.value());
		Context.Validator valueValidator = validator.forChild(".value");

		if (validator.hasVisited(key)) {
			valueValidator.reportProblem(this.classAndName().getSecond() + " with ID \"" + key.location() + "\" was referenced recursively!");
		}

		else {
			ConditionManager.getAsResult(key.location())
				.flatMap(this::checkAndCast)
				.ifSuccess(condition -> condition.validate(validator.visitChild("{" + key.location() + "}", key)))
				.ifError(error -> valueValidator.reportProblem(error.message()));
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

	static <C extends Condition, M extends IReferenceMetaCondition<C>> MapCodec<M> mapCodec(Function<ResourceLocation, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("value").forGetter(IReferenceMetaCondition::value)
		).apply(instance, constructor));
	}

	static <C extends Condition, M extends IReferenceMetaCondition<C>> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function<ResourceLocation, M> constructor) {
		return StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, IReferenceMetaCondition::value,
			constructor
		);
	}

}

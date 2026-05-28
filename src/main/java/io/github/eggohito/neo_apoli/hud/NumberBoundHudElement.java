package io.github.eggohito.neo_apoli.hud;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;

public interface NumberBoundHudElement extends HudElement {

	Context.Parameter<Double> CURRENT_VALUE = Context.simpleParameterInternal("hud/value", Double.class);

	Context.Parameter<Double> MAX_VALUE = Context.simpleParameterInternal("hud/max_value", Double.class);

	Context.Parameter<Double> MIN_VALUE = Context.simpleParameterInternal("hud/min_value", Double.class);

	Optional<NumberProvider> value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@Override
	default void validate(Context.Validator validator) {

		HudElement.super.validate(validator);

		validateKeyAndField(validator, CURRENT_VALUE, value(), "value");
		validateKeyAndField(validator, MAX_VALUE, max(), "max");
		validateKeyAndField(validator, MIN_VALUE, min(), "min");

	}

	static void validateKeyAndField(Context.Validator validator, ContextKey<?> key, Optional<NumberProvider> fieldMethod, String fieldName) {

		boolean keyIsAllowed = validator.keySet().allowed().contains(key);
		boolean fieldIsPresent = fieldMethod.isPresent();

		if (keyIsAllowed == fieldIsPresent) {
			validator.reportProblem("Either the parameter \"" + key.name() + "\" must be provided or the field \"" + fieldName + "\" be defined" + (fieldIsPresent ? ", not both" : "") + "!");
		}

	}

}

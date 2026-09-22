package io.github.eggohito.neo_apoli.hud.element;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;

public interface NumberBoundHudElement extends HudElement {

	Context.Parameter<Integer> CURRENT_VALUE = NeoApoliContextParams.registerSimpleInternal("hud/value", Integer.class);

	Context.Parameter<Integer> MAX_VALUE = NeoApoliContextParams.registerSimpleInternal("hud/max_value", Integer.class);

	Context.Parameter<Integer> MIN_VALUE = NeoApoliContextParams.registerSimpleInternal("hud/min_value", Integer.class);

	Optional<IntProvider> value();

	Optional<IntProvider> min();

	Optional<IntProvider> max();

	@Override
	default void validate(Context.Validator validator) {

		HudElement.super.validate(validator);

		validateKeyAndField(validator, CURRENT_VALUE, value(), "value");
		validateKeyAndField(validator, MAX_VALUE, max(), "max");
		validateKeyAndField(validator, MIN_VALUE, min(), "min");

	}

	static void validateKeyAndField(Context.Validator validator, ContextKey<?> key, Optional<IntProvider> fieldMethod, String fieldName) {

		boolean keyIsAllowed = validator.keySet().allowed().contains(key);
		boolean fieldIsPresent = fieldMethod.isPresent();

		if (keyIsAllowed == fieldIsPresent) {
			validator.reportProblem("Either the parameter \"" + key.name() + "\" must be provided or the field \"" + fieldName + "\" be defined" + (fieldIsPresent ? ", not both" : "") + "!");
		}

	}

}

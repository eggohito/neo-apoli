package io.github.eggohito.neo_apoli.hud;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.util.context.ContextKey;

import java.util.Optional;

public interface NumberBoundHudElement extends HudElement {

	Optional<NumberProvider> value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@Override
	default void validate(Context.Validator validator) {

		HudElement.super.validate(validator);

		validateKeyAndField(validator, NeoApoliContextParams.CURRENT_VALUE, value(), "value");
		validateKeyAndField(validator, NeoApoliContextParams.MAX_VALUE, max(), "max");
		validateKeyAndField(validator, NeoApoliContextParams.MIN_VALUE, min(), "min");

	}

	static void validateKeyAndField(Context.Validator validator, ContextKey<?> key, Optional<NumberProvider> fieldMethod, String fieldName) {

		boolean keyIsAllowed = validator.keySet().allowed().contains(key);
		boolean fieldIsPresent = fieldMethod.isPresent();

		if (keyIsAllowed == fieldIsPresent) {
			validator.reportProblem("Either the parameter \"" + key.name() + "\" must be provided or the field \"" + fieldName + "\" be defined" + (fieldIsPresent ? ", not both" : "") + "!");
		}

	}

}

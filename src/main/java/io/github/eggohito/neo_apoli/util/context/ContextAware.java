package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface ContextAware {

	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of();
	}

	default void validate(Context.Validator validator) {
		validator.validate(this);
	}

}

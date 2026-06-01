package io.github.eggohito.neo_apoli.context;

import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface ContextUser extends ContextValidatable {

	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of();
	}

	@Override
	default void validate(Context.Validator validator) {
		validator.validate(this);
	}

}

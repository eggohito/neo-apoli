package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface ContextParameterHolder {

	<T> T required(ContextParameter<T> parameter);

	@Nullable
	<T> T nullable(ContextParameter<T> parameter);

	default <T> Optional<T> optional(ContextParameter<T> parameter) {
		return Optional.ofNullable(this.nullable(parameter));
	}

	default boolean hasParameter(ContextParameter<?> parameter) {
		return this.nullable(parameter) != null;
	}

	default boolean hasAllParameters(Collection<ContextParameter<?>> parameters) {
		return hasAllParameters(parameters.toArray(ContextParameter[]::new));
	}

	default boolean hasAllParameters(ContextParameter<?>... parameters) {

		for (var parameter : parameters) {

			if (!this.hasParameter(parameter)) {
				return false;
			}

		}

		return true;

	}

	default boolean hasAnyParameters(Collection<ContextParameter<?>> parameters) {
		return hasAnyParameters(parameters.toArray(ContextParameter[]::new));
	}

	default boolean hasAnyParameters(ContextParameter<?>... parameters) {

		for (var parameter : parameters) {

			if (hasParameter(parameter)) {
				return true;
			}

		}

		return false;

	}

}

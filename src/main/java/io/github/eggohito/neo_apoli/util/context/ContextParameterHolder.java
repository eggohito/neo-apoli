package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface ContextParameterHolder {

	<T> T required(ContextKey<T> parameter);

	@Nullable
	<T> T nullable(ContextKey<T> parameter);

	default <T> Optional<T> optional(ContextKey<T> parameter) {
		return Optional.ofNullable(this.nullable(parameter));
	}

	default boolean hasParameter(ContextKey<?> parameter) {
		return this.nullable(parameter) != null;
	}

	default boolean hasAllParameters(Collection<ContextKey<?>> parameters) {
		return hasAllParameters(parameters.toArray(ContextKey[]::new));
	}

	default boolean hasAllParameters(ContextKey<?>... parameters) {

		for (var parameter : parameters) {

			if (!this.hasParameter(parameter)) {
				return false;
			}

		}

		return true;

	}

	default boolean hasAnyParameters(Collection<ContextKey<?>> parameters) {
		return hasAnyParameters(parameters.toArray(ContextKey[]::new));
	}

	default boolean hasAnyParameters(ContextKey<?>... parameters) {

		for (var parameter : parameters) {

			if (hasParameter(parameter)) {
				return true;
			}

		}

		return false;

	}

}

package io.github.eggohito.neo_apoli.context;

import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface ContextParamsHolder {

	ContextKeySet toKeySet();

	@Nullable
	<T> T getNullable(ContextKey<T> parameter);

	default <T> T getRequired(ContextKey<T> parameter) {

		T object = this.getNullable(parameter);
		if (object == null) {
			throw new NoSuchElementException(parameter.name().toString());
		}

		return object;

	}

	default <T> Optional<T> getOptional(ContextKey<T> parameter) {
		return Optional.ofNullable(this.getNullable(parameter));
	}

	default boolean hasParameter(ContextKey<?> parameter) {
		return this.getNullable(parameter) != null;
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

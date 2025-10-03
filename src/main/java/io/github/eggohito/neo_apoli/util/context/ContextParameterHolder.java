package io.github.eggohito.neo_apoli.util.context;

import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.Nullable;

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

}

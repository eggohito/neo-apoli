package io.github.eggohito.neo_apoli.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FloatConsumer {

	void accept(float value);

	default FloatConsumer andThen(@NotNull FloatConsumer after) {
		return value -> {
			this.accept(value);
			after.accept(value);
		};
	}

}

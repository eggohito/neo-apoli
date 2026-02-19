package io.github.eggohito.neo_apoli.util;

import java.util.Objects;

public interface BiIntegerConsumer<T> {

	void accept(int integer, T t);

	default BiIntegerConsumer<T> andThen(BiIntegerConsumer<? super T> after) {

		Objects.requireNonNull(after);

		return (integer, t) ->  {
			accept(integer, t);
			after.accept(integer, t);
		};

	}

}

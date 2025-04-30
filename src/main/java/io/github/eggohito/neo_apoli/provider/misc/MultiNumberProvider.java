package io.github.eggohito.neo_apoli.provider.misc;

import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;

import java.util.List;
import java.util.function.BiFunction;

public interface MultiNumberProvider extends NumberProvider {

	List<NumberProvider> numbers();

	String getPath();

	default <N extends Number> N iterateAndProcess(Context context, BiFunction<NumberProvider, Context, N> valueGetter, BiFunction<N, N, N> processor, N initialValue) {

		N result = initialValue;
		boolean init = false;

		for (int i = 0; i < numbers().size(); i++) {

			NumberProvider number = numbers().get(i);
			N value = valueGetter.apply(number, context.makeChild(getPath() + "[" + i + "]"));

			if (init) {
				result = processor.apply(result, value);
			}

			else {
				result = value;
			}

			init = true;

		}

		return result;

	}

	@Override
	default void validate(ErrorReporter reporter) {

		for (int i = 0; i < numbers().size(); i++) {
			numbers().get(i).validate(reporter.makeChild(getPath() + "[" + i + "]"));
		}

	}

}

package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ValueProvider<V> implements ContextAware, StringDisplayable {

	public abstract ValueProviderType<?> getType();

	public abstract V next(Context context);

	protected static <V> V provideValue(String name, Context context, Function<Context, V> provider, Supplier<V> defaultValue) {

		ErrorReporter reporter = context.getReporter();
		String fullPath = reporter.getFullPath();

		V value = defaultValue.get();
		Exception exception = null;

		try {
			value = provider.apply(context);
		}

		catch (Exception e) {
			exception = e;
		}

		if (exception != null || (reporter.isRoot() && reporter.hasAnyErrors())) {

			if (exception != null) {
				NeoApoli.LOGGER.error("Critical error trying to provide a {} value for path {}: {}", name, fullPath, exception);
			}

			else {
				NeoApoli.LOGGER.warn("Couldn't properly provide a {} value for path {} due to error(s) {}", name, fullPath, reporter.getErrorsAsString());
			}

		}

		return value;

	}

}

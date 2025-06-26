package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;

import java.util.function.Function;

public abstract class ValueProvider<V> implements ContextAware, StringDisplayable {

	public abstract ValueProviderType<?> getType();

	public abstract V next(Context context);

	protected static <V> V provideValue(String name, Context context, Function<Context, V> provider) {

		ErrorReporter reporter = context.getReporter();
		V value = provider.apply(context);

		if (reporter.isRoot() && reporter.hasAnyErrors()) {
			NeoApoli.LOGGER.warn("Couldn't properly provide a {} value for path {} due to error(s) {}", name, reporter.getFullPath(), reporter.getErrorsAsString());
		}

		return value;

	}

}

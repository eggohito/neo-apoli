package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import org.slf4j.event.Level;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ValueProvider<V> implements ContextAware, StringDisplayable {

	public abstract ValueProviderType<?> getType();

	public abstract V next(Context context);

	protected final <VALUE> VALUE provideValue(String name, Context context, Function<Context, VALUE> valueGetter, Supplier<VALUE> defaultValue) {

		ErrorReporter reporter = context.getReporter();
		String fullPath = reporter.getFullPath();

		VALUE value = defaultValue.get();
		Exception exception = null;

		try {

			if (context.markActive(this)) {
				value = valueGetter.apply(context);
			}

			else {
				NeoApoli.logOnce(Level.WARN, "Recursively tried to provide a " + name + " value for path " + fullPath + "!");
			}

		}

		catch (Exception e) {
			exception = e;
		}

		finally {
			context.markInActive(this);
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

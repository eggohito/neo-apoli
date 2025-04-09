package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;

public interface ObjectValueProvider<T> extends ValueProvider {

	T get(ValueProviderContext context);

}

package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import org.jetbrains.annotations.NotNull;

public interface ValueProvider<T> extends ContextAware, StringDisplayable {

	ValueProviderType<?> getType();

	@NotNull
	T next(Context context);

}

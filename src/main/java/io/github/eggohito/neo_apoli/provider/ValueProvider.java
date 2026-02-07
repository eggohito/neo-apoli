package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import org.jetbrains.annotations.NotNull;

public interface ValueProvider<T> extends ContextUser {

	ValueProviderType<?> getType();

	@NotNull
	T next(Context context);

}

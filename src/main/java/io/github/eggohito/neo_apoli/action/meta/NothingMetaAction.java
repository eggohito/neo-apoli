package io.github.eggohito.neo_apoli.action.meta;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;

public interface NothingMetaAction<T extends ActionType<?>> extends Action<T> {

	@Override
	default void execute(Context context) {

	}

}

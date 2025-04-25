package io.github.eggohito.neo_apoli.action;

import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;

public interface Action<T extends ActionType<?>> extends ContextAware {

	String TYPE_KEY = "type";

	T getType();

	void execute(Context context);

}

package io.github.eggohito.neo_apoli.condition;

import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;

public interface Condition<T extends ConditionType<?>> extends ContextAware {

	String TYPE_KEY = "type";

	T getType();

	boolean test(Context context);

}

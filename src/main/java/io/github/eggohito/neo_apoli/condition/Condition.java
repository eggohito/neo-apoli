package io.github.eggohito.neo_apoli.condition;

import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.ContextAware;

public interface Condition<CX extends ConditionContext, CT extends ConditionType<?>> extends ContextAware {

	String TYPE_KEY = "type";

	CT getType();

	boolean test(ErrorReporter reporter, CX context);

}

package io.github.eggohito.neo_apoli.condition;

import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.Validatable;

import java.util.function.Predicate;

public interface Condition<CX extends ConditionContext, CT extends ConditionType<?>> extends Predicate<CX>, Validatable {

	String TYPE_KEY = "type";

	CT getType();

	@Override
	boolean test(CX context);

}

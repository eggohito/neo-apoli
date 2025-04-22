package io.github.eggohito.neo_apoli.condition.meta;

import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;

import java.util.ListIterator;

public interface AnyOfMetaCondition<CX extends ConditionContext, CC extends Condition<CX, CT>, CT extends ConditionType<?>> extends MultiMetaCondition<CX, CC, CT>, Condition<CX, CT> {

	@Override
	default boolean test(ErrorReporter reporter, CX context) {

		ListIterator<CC> conditionIterator = conditions().listIterator();

		while (conditionIterator.hasNext()) {

			ErrorReporter conditionReporter = reporter.makeChild("conditions[" + conditionIterator.nextIndex() + "]");

			if (conditionIterator.next().test(conditionReporter, context)) {
				return true;
			}

		}

		return false;

	}

}

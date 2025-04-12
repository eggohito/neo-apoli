package io.github.eggohito.neo_apoli.condition.meta;

import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;

public interface AnyOfMetaCondition<CX extends ConditionContext, CC extends Condition<CX, CT>, CT extends ConditionType<?>> extends MultiMetaCondition<CX, CC, CT>, Condition<CX, CT> {

	@Override
	default boolean test(CX context) {

		for (CC condition : conditions()) {

			if (condition.test(context)) {
				return true;
			}

		}

		return false;

	}

}

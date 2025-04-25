package io.github.eggohito.neo_apoli.condition.meta;

import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.Context;

public interface AnyOfMetaCondition<C extends Condition<T>, T extends ConditionType<?>> extends MultiMetaCondition<C, T>, Condition<T> {

	@Override
	default boolean test(Context context) {

		for (int i = 0; i < conditions().size(); i++) {

			if (conditions().get(i).test(context.makeChild("conditions[" + i + "]"))) {
				return true;
			}

		}

		return false;

	}

}

package io.github.eggohito.neo_apoli.condition;

import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.loot.context.LootContextTypes;

public interface Condition<CX extends ConditionContext, CT extends ConditionType<?>> extends ContextAware {

	String TYPE_KEY = "type";

	CT getType();

	boolean test(ErrorReporter reporter, CX context);

	default boolean test(CX context) {
		return test(new ErrorReporter(LootContextTypes.EMPTY), context);
	}

}

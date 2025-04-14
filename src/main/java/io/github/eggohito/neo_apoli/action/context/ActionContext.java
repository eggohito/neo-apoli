package io.github.eggohito.neo_apoli.action.context;

import io.github.eggohito.neo_apoli.condition.context.ConditionContext;

public interface ActionContext<CX extends ConditionContext> {

	CX convert();

}

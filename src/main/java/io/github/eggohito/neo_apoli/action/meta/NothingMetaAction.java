package io.github.eggohito.neo_apoli.action.meta;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;

public interface NothingMetaAction<AX extends ActionContext<?>, AT extends ActionType<?>> extends Action<AX, AT> {

	@Override
	default void accept(AX context) {

	}

}

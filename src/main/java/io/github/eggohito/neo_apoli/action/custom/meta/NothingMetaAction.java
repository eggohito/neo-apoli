package io.github.eggohito.neo_apoli.action.custom.meta;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;

public interface NothingMetaAction extends Action {

	@Override
	default void execute(Context context) {

	}

	@Override
	default void validate(Context.Validator validator) {

	}

}

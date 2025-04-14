package io.github.eggohito.neo_apoli.action;

import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.Validatable;

import java.util.function.Consumer;

public interface Action<AX extends ActionContext<?>, AT extends ActionType<?>> extends Consumer<AX>, Validatable {

	String TYPE_KEY = "type";

	AT getType();

	@Override
	void accept(AX context);

}

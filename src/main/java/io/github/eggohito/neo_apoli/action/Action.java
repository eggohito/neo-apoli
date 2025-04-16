package io.github.eggohito.neo_apoli.action;

import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.loot.context.LootContextTypes;

public interface Action<AX extends ActionContext<?>, AT extends ActionType<?>> extends ContextAware {

	String TYPE_KEY = "type";

	AT getType();

	void execute(ErrorReporter reporter, AX context);

	default void execute(AX context) {
		this.execute(new ErrorReporter(LootContextTypes.EMPTY), context);
	}

}

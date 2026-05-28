package io.github.eggohito.neo_apoli.context;

public interface ContextExecutor extends ContextUser {

	void execute(Context context);

}

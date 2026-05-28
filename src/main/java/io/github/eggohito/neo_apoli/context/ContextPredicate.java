package io.github.eggohito.neo_apoli.context;

public interface ContextPredicate extends ContextUser {

	boolean test(Context context);

}
